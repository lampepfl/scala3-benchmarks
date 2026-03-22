export interface BenchmarkCategory {
  name: string;
  description: string;
  benchmarks: string[];
}

export const benchmarkCategories: BenchmarkCategory[] = [
  {
    name: "Compilation: Big",
    description: "",
    benchmarks: [
      "CompilationBenchmarksBigNightly",
      "CompilationBenchmarksBigWeeky",
      "CompilationBenchmarksBig36Nightly",
    ],
  },
  {
    name: "Compilation: Small",
    description: "",
    benchmarks: [
      "CompilationBenchmarksSmallNightly",
      "CompilationBenchmarksSmallWeekly",
    ],
  },
  {
    name: "Compilation: Xsbti",
    description: "",
    benchmarks: ["CompilationBenchmarksXsbtiWeekly"],
  },
  {
    name: "Runtime: Are We Fast Yet?",
    description: "",
    benchmarks: ["RuntimeBenchmarksAwfyWeekly"],
  },
  {
    name: "Runtime: Optimizer",
    description: "",
    benchmarks: [
      "RuntimeBenchmarksOptimizerSmallNightly",
      "RuntimeBenchmarksOptimizerSmallWeekly",
    ],
  },
  {
    name: "Runtime: Libraries",
    description: "",
    benchmarks: ["RuntimeBenchmarksLibsWeekly"],
  },
];
