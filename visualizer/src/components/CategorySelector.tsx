import { ActionList, ActionMenu } from "@primer/react";
import { benchmarkCategories } from "../benchmarkCategories";

interface CategorySelectorProps {
  selected: string[];
  onChange: (categories: string[]) => void;
}

const ALL_NAMES = benchmarkCategories.map((c) => c.name);

export default function CategorySelector({
  selected,
  onChange,
}: CategorySelectorProps) {
  // Ignore stored names of categories that no longer exist.
  const current = ALL_NAMES.filter((name) => selected.includes(name));
  const summary =
    current.length === ALL_NAMES.length
      ? "All"
      : current.length === 0
        ? "None"
        : current.length === 1
          ? current[0]
          : `${current.length} selected`;

  const toggle = (name: string) =>
    onChange(
      ALL_NAMES.filter((n) => (n === name ? !current.includes(n) : current.includes(n))),
    );

  return (
    <ActionMenu>
      <ActionMenu.Button size="small">
        <span className="color-fg-muted">Categories:</span>{" "}
        <span className="color-fg-default">{summary}</span>
      </ActionMenu.Button>
      <ActionMenu.Overlay>
        <ActionList selectionVariant="multiple" role="menu">
          {ALL_NAMES.map((name) => (
            <ActionList.Item
              key={name}
              role="menuitemcheckbox"
              selected={current.includes(name)}
              aria-checked={current.includes(name)}
              onSelect={() => toggle(name)}
            >
              {name}
            </ActionList.Item>
          ))}
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  );
}
