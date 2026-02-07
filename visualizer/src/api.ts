import { csvParse } from "d3-dsv";
import type { AggregatedRow, AllBenchmarks, SuiteBenchmarks } from "./types";

const GITHUB_API_BASE =
  "https://api.github.com/repos/lampepfl/scala3-benchmarks-data/contents";
const DATA_BASE = "https://lampepfl.github.io/scala3-benchmarks-data";

export async function listDirectory(path: string): Promise<string[]> {
  const response = await fetch(`${GITHUB_API_BASE}/${path}`);
  if (!response.ok)
    throw new Error(`Failed to list ${path}: ${response.status}`);
  const entries: { name: string; type: string }[] = await response.json();
  return entries.map((e) => e.name);
}

export async function fetchBenchmarkCsv(
  machine: string,
  jvm: string,
  version: string,
  metric: string,
  suite: string,
  benchmark: string,
): Promise<AggregatedRow[]> {
  const path = `aggregated/${machine}/${jvm}/${version}/${metric}/${suite}/${benchmark}`;
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
): Promise<AllBenchmarks> {
  const suites = await listDirectory(
    `aggregated/${machine}/${jvm}/${version}/${metric}`,
  );
  const result: AllBenchmarks = new Map();

  await Promise.all(
    suites.map(async (suite) => {
      const benchmarkFiles = await listDirectory(
        `aggregated/${machine}/${jvm}/${version}/${metric}/${suite}`,
      );
      const suiteBenchmarks: SuiteBenchmarks = new Map();

      await Promise.all(
        benchmarkFiles.map(async (file) => {
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
