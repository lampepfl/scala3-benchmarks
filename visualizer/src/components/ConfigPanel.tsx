import { Stack } from "@primer/react";
import type { Config } from "../types";
import { PERIODS } from "../types";
import SelectorOption from "./SelectorOption";
import DisplayOptions from "./DisplayOptions";
import CategorySelector from "./CategorySelector";

interface ConfigPanelProps {
  config: Config;
  onConfigChange: (config: Config) => void;
  metrics: string[];
}

export default function ConfigPanel({
  config,
  onConfigChange,
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
      <CategorySelector
        selected={config.categories}
        onChange={(categories) => onConfigChange({ ...config, categories })}
      />
      <SelectorOption
        label="Period"
        items={PERIODS.map((p) => p.label)}
        selected={PERIODS.find((p) => p.id === config.period)?.label ?? ""}
        onSelectedChange={(label) => {
          const period = PERIODS.find((p) => p.label === label);
          if (period) onConfigChange({ ...config, period: period.id });
        }}
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
