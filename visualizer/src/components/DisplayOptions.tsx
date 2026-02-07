import { useState } from "react";
import { SelectPanel, Button } from "@primer/react";
import type { SelectPanelItemInput } from "@primer/react";

interface DisplayOptionsProps {
  yAxisAtZero: boolean;
  movingAverage: boolean;
  errorBars: boolean;
  onChange: (key: "yAxisAtZero" | "movingAverage" | "errorBars", value: boolean) => void;
}

const OPTIONS: { id: string; key: "yAxisAtZero" | "movingAverage" | "errorBars"; text: string }[] = [
  { id: "yAxisAtZero", key: "yAxisAtZero", text: "Y-axis at 0" },
  { id: "movingAverage", key: "movingAverage", text: "Moving average" },
  { id: "errorBars", key: "errorBars", text: "Error bars" },
];

export default function DisplayOptions({
  yAxisAtZero,
  movingAverage,
  errorBars,
  onChange,
}: DisplayOptionsProps) {
  const [open, setOpen] = useState(false);

  const values: Record<string, boolean> = { yAxisAtZero, movingAverage, errorBars };

  const items: SelectPanelItemInput[] = OPTIONS.map((opt) => ({
    text: opt.text,
    id: opt.id,
  }));

  const selected = items.filter((item) => values[item.id as string]);

  return (
    <SelectPanel
      title="Display options"
      renderAnchor={({ ...anchorProps }) => (
        <Button {...anchorProps} size="small">Display options</Button>
      )}
      open={open}
      onOpenChange={(newOpen) => setOpen(newOpen)}
      onFilterChange={() => {}}
      items={items}
      selected={selected}
      onSelectedChange={(selectedItems: SelectPanelItemInput[]) => {
        for (const opt of OPTIONS) {
          const isSelected = selectedItems.some((s) => s.id === opt.id);
          if (isSelected !== values[opt.id]) {
            onChange(opt.key, isSelected);
          }
        }
      }}
    />
  );
}
