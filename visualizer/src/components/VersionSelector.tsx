import { useState } from "react";
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

  // Primer's default filter only keeps items that start with the input; keep
  // those that contain it anywhere instead (e.g. a commit hash or a date).
  // Primer clears its input after a selection and on Escape without calling
  // onChange, so the query is cleared at the same points.
  const [query, setQuery] = useState("");
  const filterFn = (item: { text?: string }) =>
    (item.text ?? "").toLowerCase().includes(query.trim().toLowerCase());

  return (
    <div className="mb-3" style={{ maxWidth: 600 }}>
      <FormControl>
        <FormControl.Label>Versions to compare</FormControl.Label>
        <Autocomplete>
          <Autocomplete.Input
            size="small"
            onChange={(e) => setQuery(e.currentTarget.value)}
            onKeyDown={(e) => {
              if (e.key === "Escape") setQuery("");
            }}
          />
          <Autocomplete.Overlay>
            <Autocomplete.Menu
              items={items}
              selectedItemIds={selectedVersions}
              selectionVariant="multiple"
              filterFn={filterFn}
              onSelectedChange={(items) => {
                setQuery("");
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
