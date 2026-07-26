import { Heading, Stack } from "@primer/react";
import type { AllBenchmarks, AggregatedRow, Config } from "../types";
import { benchmarkCategories, suiteBase } from "../benchmarkCategories";
import BenchmarkChart from "./BenchmarkChart";

interface BenchmarkChartListProps {
  data: AllBenchmarks;
  config: Config;
  colorMode: "day" | "night";
}

export default function BenchmarkChartList({
  data,
  config,
  colorMode,
}: BenchmarkChartListProps) {
  return (
    <Stack direction="vertical" gap="normal">
      {benchmarkCategories.map((category) => {
        // Collect benchmarks from all suites in this category, merged per
        // (base suite, benchmark) so that history split across Nightly and
        // Weekly variants joins up, while a same-named benchmark in a
        // different base suite stays separate.
        const merged = new Map<
          string,
          { benchmarkName: string; rows: AggregatedRow[] }
        >();
        for (const [suiteName, suite] of data) {
          const base = suiteBase(suiteName);
          if (!category.suites.includes(base)) continue;
          for (const [benchmarkName, rows] of suite) {
            const key = `${base}/${benchmarkName}`;
            const existing = merged.get(key);
            if (existing) {
              existing.rows = existing.rows.concat(rows);
            } else {
              merged.set(key, { benchmarkName, rows });
            }
          }
        }

        if (merged.size === 0) return null;

        // Sort benchmarks alphabetically by name
        const allBenchmarks = [...merged.entries()].sort(
          ([, a], [, b]) => a.benchmarkName.localeCompare(b.benchmarkName),
        );

        return (
          <div key={category.name}>
            <Heading as="h2" variant="large" style={{ marginTop: 24, marginBottom: 16 }}>
              {category.name}
            </Heading>
            {allBenchmarks.map(([key, { benchmarkName, rows }]) => (
              <div key={key} style={{ marginBottom: 32 }}>
                <Heading as="h3" variant="medium" style={{ marginBottom: 4 }}>
                  {benchmarkName}
                </Heading>
                <BenchmarkChart
                  title={benchmarkName}
                  data={rows}
                  config={config}
                  colorMode={colorMode}
                />
              </div>
            ))}
          </div>
        );
      })}
    </Stack>
  );
}
