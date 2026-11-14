import type { Period } from "./types";

/** Build date (YYYY-MM-DD) of a nightly version like "3.8.3-RC1-bin-20260206-7d9042f-NIGHTLY". */
export function extractDate(version: string): string {
  const match = version.match(/(\d{8})/);
  if (!match) return version.slice(0, 10);
  const d = match[1];
  return `${d.slice(0, 4)}-${d.slice(4, 6)}-${d.slice(6, 8)}`;
}

/** First date (YYYY-MM-DD) displayed for a period; "" displays everything. */
export function periodStart(period: Period, now: Date = new Date()): string {
  if (period === "all") return "";
  const start = new Date(now);
  start.setUTCMonth(start.getUTCMonth() - 4);
  return start.toISOString().slice(0, 10);
}
