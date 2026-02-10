import { useEffect, useState, useCallback } from "react";
import {
  BaseStyles,
  ThemeProvider,
  PageHeader,
  Spinner,
  Stack,
  UnderlineNav,
} from "@primer/react";
import type { Config, AllBenchmarks, ComparisonData } from "./types";
import { DEFAULT_CONFIG } from "./types";
import { fetchDataIndex, fetchAllBenchmarks, fetchComparisonData } from "./api";
import type { DataIndex } from "./api";
import { useHashRouter } from "./hooks/useHashRouter";
import ConfigPanel from "./components/ConfigPanel";
import BenchmarkChartList from "./components/BenchmarkChartList";
import VersionSelector from "./components/VersionSelector";
import ComparisonChartList from "./components/ComparisonChartList";

const STORAGE_KEY = "visualizer-config";

function loadConfig(): Config {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) return { ...DEFAULT_CONFIG, ...JSON.parse(stored) };
  } catch {
    // ignore
  }
  return DEFAULT_CONFIG;
}

function saveConfig(config: Config) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(config));
}

/** Only updates config if a value actually changed; avoids re-renders. */
function patchConfig(
  setConfig: React.Dispatch<React.SetStateAction<Config>>,
  patch: Partial<Config>,
) {
  setConfig((prev) => {
    const changed = (Object.keys(patch) as (keyof Config)[]).some(
      (k) => prev[k] !== patch[k],
    );
    if (!changed) return prev;
    const next = { ...prev, ...patch };
    saveConfig(next);
    return next;
  });
}

/** Pick a default: use current value if available, otherwise first item. */
function pickDefault(current: string, available: string[]): string {
  if (available.includes(current)) return current;
  return available[available.length - 1] ?? "";
}

export default function App() {
  const [config, setConfig] = useState<Config>(loadConfig);
  const [index, setIndex] = useState<DataIndex | null>(null);
  const [route, setRoute] = useHashRouter();

  // Time series state
  const [data, setData] = useState<AllBenchmarks>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Comparison state
  const [compareData, setCompareData] = useState<ComparisonData>(new Map());
  const [compareLoading, setCompareLoading] = useState(false);

  const handleConfigChange = useCallback((newConfig: Config) => {
    setConfig((prev) => {
      const changed = (Object.keys(newConfig) as (keyof Config)[]).some(
        (k) => prev[k] !== newConfig[k],
      );
      if (!changed) return prev;
      saveConfig(newConfig);
      return newConfig;
    });
  }, []);

  // Fetch the full data index on mount (single API call)
  useEffect(() => {
    let active = true;
    fetchDataIndex()
      .then((idx) => {
        if (!active) return;
        setIndex(idx);
        // Validate/fix config against what's actually available
        const machine = pickDefault(config.machine, idx.machines);
        const jvms = idx.jvms[machine] ?? [];
        const jvm = pickDefault(config.jvm, jvms);
        const versions = idx.versions[`${machine}/${jvm}`] ?? [];
        const minorVersion = pickDefault(config.minorVersion, versions);
        const metrics =
          idx.metrics[`${machine}/${jvm}/${minorVersion}`] ?? [];
        const metric = pickDefault(config.metric, metrics);
        patchConfig(setConfig, { machine, jvm, minorVersion, metric });
      })
      .catch((e) => {
        if (!active) return;
        setError(e.message);
        setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Fetch aggregated benchmark CSVs when config or index changes (time series view)
  useEffect(() => {
    if (
      !index ||
      !config.machine ||
      !config.jvm ||
      !config.minorVersion ||
      !config.metric
    )
      return;
    let active = true;
    setLoading(true);
    setError(null);
    fetchAllBenchmarks(
      config.machine,
      config.jvm,
      config.minorVersion,
      config.metric,
      index,
    )
      .then((result) => {
        if (!active) return;
        setData(result);
        setLoading(false);
      })
      .catch((e) => {
        if (!active) return;
        setError(e.message);
        setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [index, config.machine, config.jvm, config.minorVersion, config.metric]);

  // Fetch raw comparison data when in compare mode and versions change
  useEffect(() => {
    if (
      route.view !== "compare" ||
      route.compareVersions.length < 2 ||
      !index ||
      !config.machine ||
      !config.jvm ||
      !config.minorVersion
    )
      return;
    let active = true;
    setCompareLoading(true);
    fetchComparisonData(
      config.machine,
      config.jvm,
      config.minorVersion,
      route.compareVersions,
      index,
    )
      .then((result) => {
        if (!active) return;
        setCompareData(result);
        setCompareLoading(false);
      })
      .catch((e) => {
        if (!active) return;
        setError(e.message);
        setCompareLoading(false);
      });
    return () => {
      active = false;
    };
  }, [route.view, route.compareVersions, config.machine, config.jvm, config.minorVersion, index]);

  // Derive available options from the index
  const machines = index?.machines ?? [];
  const jvms = index?.jvms[config.machine] ?? [];
  const versions = index?.versions[`${config.machine}/${config.jvm}`] ?? [];
  const metrics =
    index?.metrics[
      `${config.machine}/${config.jvm}/${config.minorVersion}`
    ] ?? [];
  const rawVersions =
    index?.rawVersions[
      `${config.machine}/${config.jvm}/${config.minorVersion}`
    ] ?? [];

  const isCompare = route.view === "compare";

  return (
    <ThemeProvider>
      <BaseStyles>
          <div className="px-4 pt-3">
            <PageHeader>
              <PageHeader.TitleArea>
                <PageHeader.Title>Scala 3 Benchmarks</PageHeader.Title>
              </PageHeader.TitleArea>
            </PageHeader>
          </div>
          <UnderlineNav aria-label="Views">
            <UnderlineNav.Item
              as="button"
              aria-current={!isCompare ? "page" : undefined}
              onSelect={() =>
                setRoute({ view: "timeseries", compareVersions: [] })
              }
            >
              Time Series
            </UnderlineNav.Item>
            <UnderlineNav.Item
              as="button"
              aria-current={isCompare ? "page" : undefined}
              onSelect={() =>
                setRoute({
                  view: "compare",
                  compareVersions: route.compareVersions,
                })
              }
            >
              Compare
            </UnderlineNav.Item>
          </UnderlineNav>

          <div className="p-4">
          <ConfigPanel
            config={config}
            onConfigChange={handleConfigChange}
            machines={machines}
            jvms={jvms}
            versions={versions}
            metrics={metrics}
            hideMetric={isCompare}
            hideDisplayOptions={isCompare}
          />

          {error && (
            <p style={{ color: "var(--fgColor-danger)" }}>{error}</p>
          )}

          {isCompare ? (
            <>
              <VersionSelector
                availableVersions={rawVersions}
                selectedVersions={route.compareVersions}
                onSelectedVersionsChange={(newVersions) =>
                  setRoute({
                    view: "compare",
                    compareVersions: newVersions,
                  })
                }
              />
              {route.compareVersions.length < 2 ? (
                <p>Select at least 2 versions to compare.</p>
              ) : (
                <ComparisonChartList
                  data={compareData}
                  versions={route.compareVersions}
                  loading={compareLoading}
                />
              )}
            </>
          ) : loading ? (
            <Stack align="center" padding="spacious">
              <Spinner size="large" />
              <span>Loading benchmark data...</span>
            </Stack>
          ) : data.size === 0 ? (
            <p>No benchmark data found for this configuration.</p>
          ) : (
            <BenchmarkChartList data={data} config={config} />
          )}
          </div>
      </BaseStyles>
    </ThemeProvider>
  );
}
