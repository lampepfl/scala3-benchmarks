import { ActionList, ActionMenu } from "@primer/react";
import type { Config } from "../types";

/** Keys of the boolean (on/off) options in the config. */
export type DisplayOptionKey = {
  [K in keyof Config]: Config[K] extends boolean ? K : never;
}[keyof Config];

const LABELS: Record<DisplayOptionKey, string> = {
  yAxisAtZero: "Y-axis at 0",
  movingAverage: "Moving average",
  errorBars: "Error bars",
  hideOutliers: "Hide outliers",
};

interface DisplayOptionsProps {
  /** Options to list, in order. */
  keys: DisplayOptionKey[];
  config: Config;
  onChange: (key: DisplayOptionKey, value: boolean) => void;
}

export default function DisplayOptions({
  keys,
  config,
  onChange,
}: DisplayOptionsProps) {
  return (
    <ActionMenu>
      <ActionMenu.Button size="small">Display options</ActionMenu.Button>
      <ActionMenu.Overlay>
        <ActionList selectionVariant="multiple" role="menu">
          {keys.map((key) => (
            <ActionList.Item
              key={key}
              role="menuitemcheckbox"
              selected={config[key]}
              aria-checked={config[key]}
              onSelect={() => onChange(key, !config[key])}
            >
              {LABELS[key]}
            </ActionList.Item>
          ))}
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  );
}
