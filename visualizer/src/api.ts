import { csvParse } from "d3-dsv";
import type { AggregatedRow, AllBenchmarks, SuiteBenchmarks, RawSuiteData, ComparisonData } from "./types";

const GITHUB_API_BASE =
  "https://api.github.com/repos/lampepfl/scala3-benchmarks-data";
const DATA_BASE = "https://lampepfl.github.io/scala3-benchmarks-data";

/**
 * Parsed representation of the aggregated directory tree.
 * Path structure: aggregated/<machine>/<jvm>/<version>/<metric>/<suite>/<benchmark>.csv
 */
export interface DataIndex {
  machines: string[];
  jvms: Record<string, string[]>; // machine → jvms
  versions: Record<string, string[]>; // machine/jvm → versions
  metrics: Record<string, string[]>; // machine/jvm/version → metrics
  /** machine/jvm/version/metric → suite → benchmark files */
  suites: Record<string, Record<string, string[]>>;
  /** machine/jvm/patchVersion → version strings (from raw/) */
  rawVersions: Record<string, string[]>;
  /** machine/jvm/patchVersion/version → CSV filenames (from raw/) */
  rawFiles: Record<string, string[]>;
}

/** Fetches the full aggregated tree in a single API call and parses the hierarchy. */
export async function fetchDataIndex(): Promise<DataIndex> {
  const response = await fetch(
    `${GITHUB_API_BASE}/git/trees/main?recursive=1`,
  );
  if (!response.ok)
    throw new Error(`Failed to fetch data index: ${response.status}`);
  const json: { tree: { path: string; type: string }[] } =
    await response.json();

  const paths = json.tree
    .filter((e) => e.path.startsWith("aggregated/") && e.path.endsWith(".csv"))
    .map((e) => e.path.replace("aggregated/", "").split("/"));

  const machineSet = new Set<string>();
  const jvmSets: Record<string, Set<string>> = {};
  const versionSets: Record<string, Set<string>> = {};
  const metricSets: Record<string, Set<string>> = {};
  const suitesMap: Record<string, Record<string, string[]>> = {};

  for (const parts of paths) {
    if (parts.length !== 6) continue;
    const [machine, jvm, version, metric, suite, file] = parts;

    machineSet.add(machine);

    (jvmSets[machine] ??= new Set()).add(jvm);

    const mjKey = `${machine}/${jvm}`;
    (versionSets[mjKey] ??= new Set()).add(version);

    const mjvKey = `${mjKey}/${version}`;
    (metricSets[mjvKey] ??= new Set()).add(metric);

    const fullKey = `${mjvKey}/${metric}`;
    ((suitesMap[fullKey] ??= {})[suite] ??= []).push(file);
  }

  // Parse raw/ paths: raw/<machine>/<jvm>/<patchVersion>/<version>/<file>.csv
  const rawPaths = json.tree
    .filter((e) => e.path.startsWith("raw/") && e.path.endsWith(".csv"))
    .map((e) => e.path.replace("raw/", "").split("/"));

  const rawVersionSets: Record<string, Set<string>> = {};
  const rawFilesMap: Record<string, string[]> = {};

  for (const parts of rawPaths) {
    if (parts.length !== 5) continue;
    const [machine, jvm, patchVersion, version, file] = parts;
    const key = `${machine}/${jvm}/${patchVersion}`;
    (rawVersionSets[key] ??= new Set()).add(version);
    ((rawFilesMap[`${key}/${version}`]) ??= []).push(file);
  }

  const toSorted = (s: Set<string>) => [...s].sort();

  return {
    machines: toSorted(machineSet),
    jvms: Object.fromEntries(
      Object.entries(jvmSets).map(([k, v]) => [k, toSorted(v)]),
    ),
    versions: Object.fromEntries(
      Object.entries(versionSets).map(([k, v]) => [k, toSorted(v)]),
    ),
    metrics: Object.fromEntries(
      Object.entries(metricSets).map(([k, v]) => [k, toSorted(v)]),
    ),
    suites: suitesMap,
    rawVersions: Object.fromEntries(
      Object.entries(rawVersionSets).map(([k, v]) => [k, toSorted(v)]),
    ),
    rawFiles: rawFilesMap,
  };
}

export async function fetchBenchmarkCsv(
  machine: string,
  jvm: string,
  version: string,
  metric: string,
  suite: string,
  file: string,
): Promise<AggregatedRow[]> {
  const path = `aggregated/${machine}/${jvm}/${version}/${metric}/${suite}/${file}`;
  const response = await fetch(`${DATA_BASE}/${path}`);
  if (!response.ok) return [];
  const text = await response.text();
  return csvParse(text).map((row) => ({
    version: row.version!,
    count: Number(row.count),
    min: Number(row.min),
    avg: Number(row.avg),
    max: Number(row.max),
  }));
}

export async function fetchAllBenchmarks(
  machine: string,
  jvm: string,
  version: string,
  metric: string,
  index: DataIndex,
): Promise<AllBenchmarks> {
  const key = `${machine}/${jvm}/${version}/${metric}`;
  const suitesForKey = index.suites[key];
  if (!suitesForKey) return new Map();

  const result: AllBenchmarks = new Map();

  await Promise.all(
    Object.entries(suitesForKey).map(async ([suite, files]) => {
      const suiteBenchmarks: SuiteBenchmarks = new Map();

      await Promise.all(
        files.map(async (file) => {
          const benchmarkName = file.replace(/\.csv$/, "");
          const data = await fetchBenchmarkCsv(
            machine,
            jvm,
            version,
            metric,
            suite,
            file,
          );
          if (data.length > 0) {
            suiteBenchmarks.set(benchmarkName, data);
          }
        }),
      );

      if (suiteBenchmarks.size > 0) {
        result.set(suite, suiteBenchmarks);
      }
    }),
  );

  return result;
}

interface RawRow {
  suite: string;
  benchmark: string;
  times: number[];
}

async function fetchRawCsv(
  machine: string,
  jvm: string,
  patchVersion: string,
  version: string,
  file: string,
): Promise<RawRow[]> {
  const path = `raw/${machine}/${jvm}/${patchVersion}/${version}/${file}`;
  const response = await fetch(`${DATA_BASE}/${path}`);
  if (!response.ok) return [];
  const text = await response.text();
  return csvParse(text).map((row) => ({
    suite: row.suite!,
    benchmark: row.benchmark!,
    times: row.times!.split(" ").map(Number).filter((n) => !isNaN(n)),
  }));
}

async function fetchRawDataForVersion(
  machine: string,
  jvm: string,
  patchVersion: string,
  version: string,
  index: DataIndex,
): Promise<RawSuiteData> {
  const key = `${machine}/${jvm}/${patchVersion}/${version}`;
  const files = index.rawFiles[key] ?? [];
  const allRows = await Promise.all(
    files.map((file) => fetchRawCsv(machine, jvm, patchVersion, version, file)),
  );
  const result: RawSuiteData = new Map();
  for (const rows of allRows) {
    for (const row of rows) {
      if (!result.has(row.suite)) result.set(row.suite, new Map());
      const suiteBenchmarks = result.get(row.suite)!;
      const existing = suiteBenchmarks.get(row.benchmark) ?? [];
      suiteBenchmarks.set(row.benchmark, existing.concat(row.times));
    }
  }
  return result;
}

export async function fetchComparisonData(
  machine: string,
  jvm: string,
  patchVersion: string,
  versions: string[],
  index: DataIndex,
): Promise<ComparisonData> {
  const entries = await Promise.all(
    versions.map(async (version) => {
      const data = await fetchRawDataForVersion(machine, jvm, patchVersion, version, index);
      return [version, data] as const;
    }),
  );
  return new Map(entries);
}
