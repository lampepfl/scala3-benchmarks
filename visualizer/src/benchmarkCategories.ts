export interface BenchmarkCategory {
  name: string;
  benchmarks: string[];
}

export const benchmarkCategories: BenchmarkCategory[] = [
  {
    name: "Compilation – Big",
    benchmarks: [
      "CompilationBenchmarksBigNightly",
      "CompilationBenchmarksBigWeekly",
      "CompilationBenchmarksBig36Nightly",
    ],
  },
  {
    name: "Compilation – Small",
    benchmarks: [
      "CompilationBenchmarksSmallNightly",
      "CompilationBenchmarksSmallWeekly",
    ],
  },
  {
    name: "Compilation – Xsbti",
    benchmarks: ["CompilationBenchmarksXsbtiWeekly"],
  },
  {
    name: "Runtime – Optimizer",
    benchmarks: [
      "RuntimeBenchmarksOptimizerSmallNightly",
      "RuntimeBenchmarksOptimizerSmallWeekly",
    ],
  },
  {
    name: "Runtime – Libraries",
    benchmarks: ["RuntimeBenchmarksLibsWeekly"],
  },
  {
    name: "Runtime – Are We Fast Yet?",
    benchmarks: ["RuntimeBenchmarksAwfyWeekly"],
  },
];
