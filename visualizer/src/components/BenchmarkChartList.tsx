import { Heading, Stack } from "@primer/react";
import type { AllBenchmarks, AggregatedRow, Config } from "../types";
import { benchmarkCategories } from "../benchmarkCategories";
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
        // Collect all benchmarks from all suites in this category
        const allBenchmarks: [string, AggregatedRow[]][] = [];
        for (const suiteName of category.benchmarks) {
          const suite = data.get(suiteName);
          if (!suite) continue;
          for (const [benchmarkName, rows] of suite) {
            allBenchmarks.push([benchmarkName, rows]);
          }
        }

        if (allBenchmarks.length === 0) return null;

        // Sort benchmarks alphabetically by name
        allBenchmarks.sort(([a], [b]) => a.localeCompare(b));

        return (
          <div key={category.name}>
            <Heading as="h2" variant="large" style={{ marginTop: 24, marginBottom: 16, textAlign: "center" }}>
              {category.name}
            </Heading>
            {allBenchmarks.map(([benchmarkName, rows]) => (
              <div key={benchmarkName} style={{ border: "1px solid var(--borderColor-default, #d0d7de)", borderRadius: 6, padding: 16, marginBottom: 32 }}>
                <Heading as="h3" variant="small" style={{ marginBottom: 4 }}>
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
