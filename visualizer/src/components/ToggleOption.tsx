import { ToggleSwitch, Stack } from "@primer/react";

interface ToggleOptionProps {
  id: string;
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}

export default function ToggleOption({
  id,
  label,
  checked,
  onChange,
}: ToggleOptionProps) {
  const labelId = `${id}-label`;
  return (
    <Stack direction="horizontal" align="center" gap="condensed">
      <span id={labelId} style={{ fontSize: 14, whiteSpace: "nowrap" }}>
        {label}
      </span>
      <ToggleSwitch
        aria-labelledby={labelId}
        checked={checked}
        onClick={() => onChange(!checked)}
        size="small"
      />
    </Stack>
  );
}
