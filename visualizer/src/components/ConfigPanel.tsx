import { Stack } from "@primer/react";
import type { Config } from "../types";
import ToggleOption from "./ToggleOption";
import SelectorOption from "./SelectorOption";

interface ConfigPanelProps {
  config: Config;
  onConfigChange: (config: Config) => void;
  machines: string[];
  jvms: string[];
  versions: string[];
  metrics: string[];
}

export default function ConfigPanel({
  config,
  onConfigChange,
  machines,
  jvms,
  versions,
  metrics,
}: ConfigPanelProps) {
  return (
    <Stack direction="vertical" gap="normal" sx={{ mb: 3 }}>
      <Stack direction="horizontal" gap="normal" wrap="wrap">
        <ToggleOption
          id="y-axis-zero"
          label="Y-axis at 0"
          checked={config.yAxisAtZero}
          onChange={(checked) =>
            onConfigChange({ ...config, yAxisAtZero: checked })
          }
        />
        <ToggleOption
          id="moving-average"
          label="Moving average"
          checked={config.movingAverage}
          onChange={(checked) =>
            onConfigChange({ ...config, movingAverage: checked })
          }
        />
        <ToggleOption
          id="error-bars"
          label="Error bars"
          checked={config.errorBars}
          onChange={(checked) =>
            onConfigChange({ ...config, errorBars: checked })
          }
        />
      </Stack>
      <Stack direction="horizontal" gap="condensed" wrap="wrap">
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
        <SelectorOption
          label="Metric"
          items={metrics}
          selected={config.metric}
          onSelectedChange={(v) => onConfigChange({ ...config, metric: v })}
        />
        <SelectorOption
          label="Minor version"
          items={versions}
          selected={config.minorVersion}
          onSelectedChange={(v) =>
            onConfigChange({ ...config, minorVersion: v })
          }
        />
      </Stack>
    </Stack>
  );
}
