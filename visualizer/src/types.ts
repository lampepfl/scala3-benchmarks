export interface AggregatedRow {
  version: string;
  count: number;
  min: number;
  avg: number;
  max: number;
}

export interface Config {
  yAxisAtZero: boolean;
  movingAverage: boolean;
  errorBars: boolean;
  machine: string;
  jvm: string;
  metric: string;
  minorVersion: string;
}

export const DEFAULT_CONFIG: Config = {
  yAxisAtZero: false,
  movingAverage: true,
  errorBars: true,
  machine: "laraquad1",
  jvm: "temurin-25",
  metric: "time",
  minorVersion: "",
};

/** Map from benchmark name to its data rows */
export type SuiteBenchmarks = Map<string, AggregatedRow[]>;

/** Map from suite name to its benchmarks */
export type AllBenchmarks = Map<string, SuiteBenchmarks>;
