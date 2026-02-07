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
  return (
    <Stack direction="vertical" gap="normal">
      {Array.from(data.entries()).map(([suiteName, benchmarks]) => (
        <div key={suiteName}>
          <Heading as="h2" variant="small" style={{ marginTop: 24, marginBottom: 8 }}>
            {suiteName}
          </Heading>
          {Array.from(benchmarks.entries()).map(([benchmarkName, rows]) => (
            <BenchmarkChart
              key={`${suiteName}-${benchmarkName}`}
              title={benchmarkName}
              data={rows}
              config={config}
            />
          ))}
        </div>
      ))}
    </Stack>
  );
}
