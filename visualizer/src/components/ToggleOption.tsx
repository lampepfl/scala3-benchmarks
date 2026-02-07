import { Checkbox, FormControl } from "@primer/react";

interface ToggleOptionProps {
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}

export default function ToggleOption({
  label,
  checked,
  onChange,
}: ToggleOptionProps) {
  return (
    <FormControl layout="horizontal">
      <Checkbox
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
      />
      <FormControl.Label>{label}</FormControl.Label>
    </FormControl>
  );
}
