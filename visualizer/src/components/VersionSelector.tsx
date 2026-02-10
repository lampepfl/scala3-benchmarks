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
    <div className="mb-3" style={{ maxWidth: 600 }}>
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
        <div className="mt-2 d-flex flex-wrap" style={{ gap: 4 }}>
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
