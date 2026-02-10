import { Stack } from "@primer/react";
import type { Config } from "../types";
import SelectorOption from "./SelectorOption";
import DisplayOptions from "./DisplayOptions";

interface ConfigPanelProps {
  config: Config;
  onConfigChange: (config: Config) => void;
  machines: string[];
  jvms: string[];
  versions: string[];
  metrics: string[];
  hideMetric?: boolean;
  hideDisplayOptions?: boolean;
}

export default function ConfigPanel({
  config,
  onConfigChange,
  machines,
  jvms,
  versions,
  metrics,
  hideMetric,
  hideDisplayOptions,
}: ConfigPanelProps) {
  return (
    <Stack
      direction="horizontal"
      gap="condensed"
      wrap="wrap"
      style={{ marginBottom: 16 }}
    >
      <SelectorOption
        label="Machine"
        items={machines}
        selected={config.machine}
        onSelectedChange={(v) => onConfigChange({ ...config, machine: v })}
      />
      <SelectorOption
        label="JVM"
        items={jvms}
        selected={config.jvm}
        onSelectedChange={(v) => onConfigChange({ ...config, jvm: v })}
      />
      {!hideMetric && (
        <SelectorOption
          label="Metric"
          items={metrics}
          selected={config.metric}
          onSelectedChange={(v) => onConfigChange({ ...config, metric: v })}
        />
      )}
      <SelectorOption
        label="Minor version"
        items={versions}
        selected={config.minorVersion}
        onSelectedChange={(v) =>
          onConfigChange({ ...config, minorVersion: v })
        }
      />
      {!hideDisplayOptions && (
        <DisplayOptions
          yAxisAtZero={config.yAxisAtZero}
          movingAverage={config.movingAverage}
          errorBars={config.errorBars}
          onChange={(key, value) =>
            onConfigChange({ ...config, [key]: value })
          }
        />
      )}
    </Stack>
  );
}
