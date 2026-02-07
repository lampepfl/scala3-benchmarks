import { useEffect, useState, useCallback } from "react";
import {
  BaseStyles,
  ThemeProvider,
  PageLayout,
  Heading,
  Spinner,
  Stack,
} from "@primer/react";
import type { Config, AllBenchmarks } from "./types";
import { DEFAULT_CONFIG } from "./types";
import { listDirectory, fetchAllBenchmarks } from "./api";
import ConfigPanel from "./components/ConfigPanel";
import BenchmarkChartList from "./components/BenchmarkChartList";

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

export default function App() {
  const [config, setConfig] = useState<Config>(loadConfig);

  const [machines, setMachines] = useState<string[]>([]);
  const [jvms, setJvms] = useState<string[]>([]);
  const [versions, setVersions] = useState<string[]>([]);
  const [metrics, setMetrics] = useState<string[]>([]);

  const [data, setData] = useState<AllBenchmarks>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleConfigChange = useCallback((newConfig: Config) => {
    setConfig(newConfig);
    saveConfig(newConfig);
  }, []);

  // Fetch machines on mount
  useEffect(() => {
    let active = true;
    listDirectory("aggregated")
      .then((names) => {
        if (!active) return;
        setMachines(names);
        if (!names.includes(config.machine) && names.length > 0) {
          handleConfigChange({ ...config, machine: names[0] });
        }
      })
      .catch((e) => active && setError(e.message));
    return () => {
      active = false;
    };
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Fetch JVMs when machine changes
  useEffect(() => {
    if (!config.machine) return;
    let active = true;
    listDirectory(`aggregated/${config.machine}`)
      .then((names) => {
        if (!active) return;
        setJvms(names);
        if (!names.includes(config.jvm) && names.length > 0) {
          handleConfigChange({ ...config, jvm: names[0] });
        }
      })
      .catch((e) => active && setError(e.message));
    return () => {
      active = false;
    };
  }, [config.machine]); // eslint-disable-line react-hooks/exhaustive-deps

  // Fetch versions when JVM changes
  useEffect(() => {
    if (!config.machine || !config.jvm) return;
    let active = true;
    listDirectory(`aggregated/${config.machine}/${config.jvm}`)
      .then((names) => {
        if (!active) return;
        setVersions(names);
        const latest = names[names.length - 1];
        if (!names.includes(config.minorVersion) && names.length > 0) {
          handleConfigChange({ ...config, minorVersion: latest });
        }
      })
      .catch((e) => active && setError(e.message));
    return () => {
      active = false;
    };
  }, [config.machine, config.jvm]); // eslint-disable-line react-hooks/exhaustive-deps

  // Fetch metrics when version changes
  useEffect(() => {
    if (!config.machine || !config.jvm || !config.minorVersion) return;
    let active = true;
    listDirectory(
      `aggregated/${config.machine}/${config.jvm}/${config.minorVersion}`,
    )
      .then((names) => {
        if (!active) return;
        setMetrics(names);
        if (!names.includes(config.metric) && names.length > 0) {
          handleConfigChange({ ...config, metric: names[0] });
        }
      })
      .catch((e) => active && setError(e.message));
    return () => {
      active = false;
    };
  }, [config.machine, config.jvm, config.minorVersion]); // eslint-disable-line react-hooks/exhaustive-deps

  // Fetch benchmark data when metric changes
  useEffect(() => {
    if (
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
  }, [config.machine, config.jvm, config.minorVersion, config.metric]);

  return (
    <ThemeProvider>
      <BaseStyles>
        <PageLayout>
          <PageLayout.Content>
            <Heading as="h1" style={{ marginBottom: 16 }}>
              Scala 3 Benchmarks
            </Heading>

            <ConfigPanel
              config={config}
              onConfigChange={handleConfigChange}
              machines={machines}
              jvms={jvms}
              versions={versions}
              metrics={metrics}
            />

            {error && (
              <p style={{ color: "var(--fgColor-danger)" }}>{error}</p>
            )}

            {loading ? (
              <Stack align="center" padding="spacious">
                <Spinner size="large" />
                <span>Loading benchmark data...</span>
              </Stack>
            ) : data.size === 0 ? (
              <p>No benchmark data found for this configuration.</p>
            ) : (
              <BenchmarkChartList data={data} config={config} />
            )}
          </PageLayout.Content>
        </PageLayout>
      </BaseStyles>
    </ThemeProvider>
  );
}
