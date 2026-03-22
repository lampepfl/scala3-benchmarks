import { Heading, Stack, Spinner } from "@primer/react";
import type { ComparisonData, RawMeasurements } from "../types";
import { benchmarkCategories } from "../benchmarkCategories";
import ComparisonChart from "./ComparisonChart";

interface ComparisonChartListProps {
  data: ComparisonData;
  versions: string[];
  loading: boolean;
  colorMode: "day" | "night";
}

export default function ComparisonChartList({
  data,
  versions,
  loading,
  colorMode,
}: ComparisonChartListProps) {
  if (loading) {
    return (
      <Stack align="center" padding="spacious">
        <Spinner size="large" />
        <span>Loading raw benchmark data...</span>
      </Stack>
    );
  }

  // Collect all suites across all versions
  const allSuites = new Set<string>();
  for (const suiteData of data.values()) {
    for (const suite of suiteData.keys()) {
      allSuites.add(suite);
    }
  }

  if (allSuites.size === 0) {
    return <p>No raw benchmark data found for the selected versions.</p>;
  }

  return (
    <Stack direction="vertical" gap="normal">
      {benchmarkCategories.map((category) => {
        // Merge all suites in this category into one RawMeasurements per version
        const mergedVersionData = new Map<string, RawMeasurements>();
        let hasBenchmarks = false;

        for (const [version, suiteData] of data) {
          const merged: RawMeasurements = new Map();
          for (const suiteName of category.benchmarks) {
            const benchmarks = suiteData.get(suiteName);
            if (!benchmarks) continue;
            for (const [bench, times] of benchmarks) {
              merged.set(bench, times);
              hasBenchmarks = true;
            }
          }
          if (merged.size > 0) {
            mergedVersionData.set(version, merged);
          }
        }

        if (!hasBenchmarks) return null;

        return (
          <div key={category.name}>
            <Heading as="h2" variant="large" style={{ marginTop: 24, marginBottom: 16 }}>
              {category.name}
            </Heading>
            <ComparisonChart
              versionData={mergedVersionData}
              versions={versions}
              colorMode={colorMode}
            />
          </div>
        );
      })}
    </Stack>
  );
}
