import { ActionList, ActionMenu } from "@primer/react";

interface SelectorOptionProps {
  label: string;
  items: string[];
  selected: string;
  onSelectedChange: (value: string) => void;
}

/** Dropdown to pick one of a few items (no filter field). */
export default function SelectorOption({
  label,
  items,
  selected,
  onSelectedChange,
}: SelectorOptionProps) {
  return (
    <ActionMenu>
      <ActionMenu.Button size="small">
        <span className="color-fg-muted">{label}:</span>{" "}
        <span className="color-fg-default">{selected || "..."}</span>
      </ActionMenu.Button>
      <ActionMenu.Overlay>
        <ActionList selectionVariant="single" role="menu" aria-label={label}>
          {items.map((item) => (
            <ActionList.Item
              key={item}
              role="menuitemradio"
              selected={item === selected}
              aria-checked={item === selected}
              onSelect={() => onSelectedChange(item)}
            >
              {item}
            </ActionList.Item>
          ))}
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  );
}
