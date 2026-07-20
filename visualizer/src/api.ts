import { csvParse } from "d3-dsv";
import type { AggregatedRow, AllBenchmarks, SuiteBenchmarks, RawSuiteData, ComparisonData } from "./types";

const GITHUB_API_BASE =
  "https://api.github.com/repos/lampepfl/scala3-benchmarks-data";
const DATA_BASE = "https://lampepfl.github.io/scala3-benchmarks-data";

/** All benchmarks currently run on a single machine and JVM. */
export const MACHINE = "laraquad1";
export const JVM = "temurin-25";

const AGGREGATED_PREFIX = `aggregated/${MACHINE}/${JVM}/`;
const RAW_PREFIX = `raw/${MACHINE}/${JVM}/`;

/**
 * Parsed representation of the data directory tree.
 * Path structure: aggregated/<machine>/<jvm>/<version>/<metric>/<suite>/<benchmark>.csv
 */
export interface DataIndex {
  versions: string[];
  /** version → metrics */
  metrics: Record<string, string[]>;
  /** version/metric → suite → benchmark files */
  suites: Record<string, Record<string, string[]>>;
  /** patchVersion/version → CSV filenames (from raw/) */
  rawFiles: Record<string, string[]>;
  /** All raw version strings (across all patch versions) */
  rawVersions: string[];
  /** version → patchVersion (reverse lookup) */
  rawVersionToPatch: Record<string, string>;
}

/** Fetches the full data tree in a single API call and parses the hierarchy. */
export async function fetchDataIndex(): Promise<DataIndex> {
  const response = await fetch(
    `${GITHUB_API_BASE}/git/trees/main?recursive=1`,
  );
  if (!response.ok)
    throw new Error(`Failed to fetch data index: ${response.status}`);
  const json: { tree: { path: string; type: string }[] } =
    await response.json();

  const paths = json.tree
    .filter(
      (e) => e.path.startsWith(AGGREGATED_PREFIX) && e.path.endsWith(".csv"),
    )
    .map((e) => e.path.replace(AGGREGATED_PREFIX, "").split("/"));

  const versionSet = new Set<string>();
  const metricSets: Record<string, Set<string>> = {};
  const suitesMap: Record<string, Record<string, string[]>> = {};

  for (const parts of paths) {
    if (parts.length !== 4) continue;
    const [version, metric, suite, file] = parts;

    versionSet.add(version);
    (metricSets[version] ??= new Set()).add(metric);
    ((suitesMap[`${version}/${metric}`] ??= {})[suite] ??= []).push(file);
  }

  // Parse raw/ paths: raw/<machine>/<jvm>/<patchVersion>/<version>/<file>.csv
  const rawPaths = json.tree
    .filter((e) => e.path.startsWith(RAW_PREFIX) && e.path.endsWith(".csv"))
    .map((e) => e.path.replace(RAW_PREFIX, "").split("/"));

  const rawFilesMap: Record<string, string[]> = {};
  const rawVersionSet = new Set<string>();
  const rawVersionToPatch: Record<string, string> = {};

  for (const parts of rawPaths) {
    if (parts.length !== 3) continue;
    const [patchVersion, version, file] = parts;
    ((rawFilesMap[`${patchVersion}/${version}`]) ??= []).push(file);
    rawVersionSet.add(version);
    rawVersionToPatch[version] = patchVersion;
  }

  // Natural sort so version-like strings order numerically ("3.9.0" < "3.10.0").
  const compareNatural = (a: string, b: string): number => {
    const ax = a.split(/(\d+)/);
    const bx = b.split(/(\d+)/);
    for (let i = 0; i < Math.max(ax.length, bx.length); i++) {
      const as = ax[i] ?? "";
      const bs = bx[i] ?? "";
      if (as === bs) continue;
      if (/^\d+$/.test(as) && /^\d+$/.test(bs)) return Number(as) - Number(bs);
      return as < bs ? -1 : 1;
    }
    return 0;
  };
  const toSorted = (s: Set<string>) => [...s].sort(compareNatural);

  return {
    versions: toSorted(versionSet),
    metrics: Object.fromEntries(
      Object.entries(metricSets).map(([k, v]) => [k, toSorted(v)]),
    ),
    suites: suitesMap,
    rawFiles: rawFilesMap,
    rawVersions: toSorted(rawVersionSet),
    rawVersionToPatch,
  };
}

export async function fetchBenchmarkCsv(
  version: string,
  metric: string,
  suite: string,
  file: string,
): Promise<AggregatedRow[]> {
  const path = `${AGGREGATED_PREFIX}${version}/${metric}/${suite}/${file}`;
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
  version: string,
  metric: string,
  index: DataIndex,
): Promise<AllBenchmarks> {
  const suitesForKey = index.suites[`${version}/${metric}`];
  if (!suitesForKey) return new Map();

  const result: AllBenchmarks = new Map();

  await Promise.all(
    Object.entries(suitesForKey).map(async ([suite, files]) => {
      const suiteBenchmarks: SuiteBenchmarks = new Map();

      await Promise.all(
        files.map(async (file) => {
          const benchmarkName = file.replace(/\.csv$/, "");
          const data = await fetchBenchmarkCsv(version, metric, suite, file);
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
  patchVersion: string,
  version: string,
  file: string,
): Promise<RawRow[]> {
  const path = `${RAW_PREFIX}${patchVersion}/${version}/${file}`;
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
  patchVersion: string,
  version: string,
  index: DataIndex,
): Promise<RawSuiteData> {
  const files = index.rawFiles[`${patchVersion}/${version}`] ?? [];
  const allRows = await Promise.all(
    files.map((file) => fetchRawCsv(patchVersion, version, file)),
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
  versions: string[],
  index: DataIndex,
): Promise<ComparisonData> {
  const entries = await Promise.all(
    versions.map(async (version) => {
      const patchVersion = index.rawVersionToPatch[version];
      if (!patchVersion) return [version, new Map() as RawSuiteData] as const;
      const data = await fetchRawDataForVersion(patchVersion, version, index);
      return [version, data] as const;
    }),
  );
  return new Map(entries);
}
