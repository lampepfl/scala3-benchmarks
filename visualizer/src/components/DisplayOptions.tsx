import { ActionList, ActionMenu } from "@primer/react";

interface DisplayOptionsProps {
  yAxisAtZero: boolean;
  movingAverage: boolean;
  errorBars: boolean;
  onChange: (key: "yAxisAtZero" | "movingAverage" | "errorBars", value: boolean) => void;
}

const OPTIONS: { key: "yAxisAtZero" | "movingAverage" | "errorBars"; label: string }[] = [
  { key: "yAxisAtZero", label: "Y-axis at 0" },
  { key: "movingAverage", label: "Moving average" },
  { key: "errorBars", label: "Error bars" },
];

export default function DisplayOptions({
  yAxisAtZero,
  movingAverage,
  errorBars,
  onChange,
}: DisplayOptionsProps) {
  const values: Record<string, boolean> = { yAxisAtZero, movingAverage, errorBars };

  return (
    <ActionMenu>
      <ActionMenu.Button size="small">Display options</ActionMenu.Button>
      <ActionMenu.Overlay>
        <ActionList selectionVariant="multiple" role="menu">
          {OPTIONS.map((opt) => (
            <ActionList.Item
              key={opt.key}
              role="menuitemcheckbox"
              selected={values[opt.key]}
              aria-checked={values[opt.key]}
              onSelect={() => onChange(opt.key, !values[opt.key])}
            >
              {opt.label}
            </ActionList.Item>
          ))}
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  );
}
