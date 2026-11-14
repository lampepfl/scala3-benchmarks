/** Quantile of sorted values, interpolating linearly between ranks. */
function quantile(sorted: number[], p: number): number {
  const i = (sorted.length - 1) * p;
  const lo = Math.floor(i);
  const hi = Math.ceil(i);
  return sorted[lo] + (sorted[hi] - sorted[lo]) * (i - lo);
}

/**
 * Tukey's "far out" fences: values below Q1 - 3 IQR or above Q3 + 3 IQR are
 * extreme outliers. Groups too small for quartiles have no outliers.
 */
export function farOutFences(values: number[]): [number, number] {
  if (values.length < 4) return [-Infinity, Infinity];
  const sorted = [...values].sort((a, b) => a - b);
  const q1 = quantile(sorted, 0.25);
  const q3 = quantile(sorted, 0.75);
  return [q1 - 3 * (q3 - q1), q3 + 3 * (q3 - q1)];
}
