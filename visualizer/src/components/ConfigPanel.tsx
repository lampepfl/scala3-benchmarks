import { Stack } from "@primer/react";
import type { Config } from "../types";
import SelectorOption from "./SelectorOption";
import DisplayOptions from "./DisplayOptions";

interface ConfigPanelProps {
  config: Config;
  onConfigChange: (config: Config) => void;
  versions: string[];
  metrics: string[];
}

export default function ConfigPanel({
  config,
  onConfigChange,
  versions,
  metrics,
}: ConfigPanelProps) {
  return (
    <Stack
      direction="horizontal"
      gap="condensed"
      wrap="wrap"
      className="mb-3"
    >
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
      <DisplayOptions
        yAxisAtZero={config.yAxisAtZero}
        movingAverage={config.movingAverage}
        errorBars={config.errorBars}
        onChange={(key, value) =>
          onConfigChange({ ...config, [key]: value })
        }
      />
    </Stack>
  );
}
