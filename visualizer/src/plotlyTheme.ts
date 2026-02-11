type ColorMode = "day" | "night";

// Primer data visualization mark colors
// https://primer.style/product/ui-patterns/data-visualization/#available-mark-colors
const LIGHT_COLORWAY = [
  "#006edb", // blue
  "#30a147", // green
  "#eb670f", // orange
  "#894ceb", // purple
  "#ce2c85", // pink
  "#179b9b", // teal
  "#866e04", // lemon
  "#df0c24", // red
];

const DARK_COLORWAY = [
  "#0576ff", // blue
  "#2f6f37", // green
  "#984b10", // orange
  "#975bf1", // purple
  "#d34591", // pink
  "#106c70", // teal
  "#977b0c", // lemon
  "#eb3342", // red
];

export function plotlyColors(colorMode: ColorMode) {
  const isDark = colorMode === "night";
  return {
    font: isDark ? "#c9d1d9" : "#1f2328",
    grid: isDark ? "#30363d" : "#e1e4e8",
    colorway: isDark ? DARK_COLORWAY : LIGHT_COLORWAY,
  };
}
