import { Heading, Stack } from "@primer/react";
import type { AllBenchmarks, Config } from "../types";
import BenchmarkChart from "./BenchmarkChart";

interface BenchmarkChartListProps {
  data: AllBenchmarks;
  config: Config;
}

export default function BenchmarkChartList({
  data,
  config,
}: BenchmarkChartListProps) {
  // Sort suites alphabetically
  const sortedSuites = Array.from(data.entries()).sort(([a], [b]) =>
    a.localeCompare(b)
  );

  return (
    <Stack direction="vertical" gap="normal">
      {sortedSuites.map(([suiteName, benchmarks]) => {
        // Sort benchmarks alphabetically within each suite
        const sortedBenchmarks = Array.from(benchmarks.entries()).sort(
          ([a], [b]) => a.localeCompare(b)
        );

        return (
          <div key={suiteName}>
            <Heading as="h2" variant="small" style={{ marginTop: 24, marginBottom: 8 }}>
              {suiteName}
            </Heading>
            {sortedBenchmarks.map(([benchmarkName, rows]) => (
              <BenchmarkChart
                key={`${suiteName}-${benchmarkName}`}
                title={benchmarkName}
                data={rows}
                config={config}
              />
            ))}
          </div>
        );
      })}
    </Stack>
  );
}
