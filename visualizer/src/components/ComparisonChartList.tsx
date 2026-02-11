import { Stack, Spinner } from "@primer/react";
import type { ComparisonData, RawMeasurements } from "../types";
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
  const sortedSuites = [...allSuites].sort();

  if (sortedSuites.length === 0) {
    return <p>No raw benchmark data found for the selected versions.</p>;
  }

  return (
    <Stack direction="vertical" gap="normal">
      {sortedSuites.map((suiteName) => {
        const versionData = new Map<string, RawMeasurements>();
        for (const [version, suiteData] of data) {
          const benchmarks = suiteData.get(suiteName);
          if (benchmarks) versionData.set(version, benchmarks);
        }

        return (
          <ComparisonChart
            key={suiteName}
            suiteName={suiteName}
            versionData={versionData}
            versions={versions}
            colorMode={colorMode}
          />
        );
      })}
    </Stack>
  );
}
