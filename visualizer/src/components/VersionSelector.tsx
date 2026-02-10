import { Autocomplete, FormControl, Token } from "@primer/react";

interface VersionSelectorProps {
  availableVersions: string[];
  selectedVersions: string[];
  onSelectedVersionsChange: (versions: string[]) => void;
}

export default function VersionSelector({
  availableVersions,
  selectedVersions,
  onSelectedVersionsChange,
}: VersionSelectorProps) {
  const items = availableVersions.map((v) => ({ text: v, id: v }));

  return (
    <div style={{ marginBottom: 16, maxWidth: 600 }}>
      <FormControl>
        <FormControl.Label>Versions to compare</FormControl.Label>
        <Autocomplete>
          <Autocomplete.Input size="small" />
          <Autocomplete.Overlay>
            <Autocomplete.Menu
              items={items}
              selectedItemIds={selectedVersions}
              selectionVariant="multiple"
              onSelectedChange={(items) => {
                if (Array.isArray(items)) {
                  onSelectedVersionsChange(items.map((item) => item.id));
                }
              }}
              aria-labelledby="version-selector-label"
            />
          </Autocomplete.Overlay>
        </Autocomplete>
      </FormControl>
      {selectedVersions.length > 0 && (
        <div style={{ display: "flex", flexWrap: "wrap", gap: 4, marginTop: 8 }}>
          {selectedVersions.map((version) => (
            <Token
              key={version}
              text={version}
              onRemove={() =>
                onSelectedVersionsChange(
                  selectedVersions.filter((v) => v !== version),
                )
              }
            />
          ))}
        </div>
      )}
    </div>
  );
}
