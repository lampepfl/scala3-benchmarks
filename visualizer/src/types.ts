export interface AggregatedRow {
  version: string;
  count: number;
  min: number;
  avg: number;
  max: number;
}

export type Period = "4m" | "all";

export const PERIODS: { id: Period; label: string }[] = [
  { id: "4m", label: "Last 4 months" },
  { id: "all", label: "All" },
];

export interface Config {
  yAxisAtZero: boolean;
  movingAverage: boolean;
  errorBars: boolean;
  metric: string;
  period: Period;
  /** Names of the benchmark categories to display. */
  categories: string[];
}

export const DEFAULT_CONFIG: Config = {
  yAxisAtZero: false,
  movingAverage: true,
  errorBars: true,
  metric: "time",
  period: "4m",
  categories: ["Compilation – Big"],
};

/** Map from benchmark name to its data rows */
export type SuiteBenchmarks = Map<string, AggregatedRow[]>;

/** Map from suite name to its benchmarks */
export type AllBenchmarks = Map<string, SuiteBenchmarks>;

export type ViewMode = "timeseries" | "compare";

/** Map: benchmark → individual measurements */
export type RawMeasurements = Map<string, number[]>;

/** Map: suite → benchmarks */
export type RawSuiteData = Map<string, RawMeasurements>;

/** Map: version → suites */
export type ComparisonData = Map<string, RawSuiteData>;
