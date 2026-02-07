import { useState } from "react";
import { SelectPanel, Button } from "@primer/react";
import type { SelectPanelItemInput } from "@primer/react";

interface SelectorOptionProps {
  label: string;
  items: string[];
  selected: string;
  onSelectedChange: (value: string) => void;
}

export default function SelectorOption({
  label,
  items,
  selected,
  onSelectedChange,
}: SelectorOptionProps) {
  const [open, setOpen] = useState(false);
  const [filter, setFilter] = useState("");

  const panelItems: SelectPanelItemInput[] = items
    .filter((name) => name.toLowerCase().includes(filter.toLowerCase()))
    .map((name) => ({
    text: name,
    id: name,
  }));

  const selectedItem = panelItems.find((item) => item.id === selected);

  return (
    <SelectPanel
      title={`Select ${label}`}
      renderAnchor={({ ...anchorProps }) => (
        <Button {...anchorProps} size="small">
          {label}: {selected || "..."}
        </Button>
      )}
      open={open}
      onOpenChange={(newOpen) => {
        setOpen(newOpen);
        if (!newOpen) setFilter("");
      }}
      onFilterChange={(value) => setFilter(value)}
      items={panelItems}
      selected={selectedItem}
      onSelectedChange={(item: SelectPanelItemInput | undefined) => {
        if (item && typeof item.id === "string") {
          onSelectedChange(item.id);
        }
        setOpen(false);
      }}
    />
  );
}
