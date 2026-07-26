export interface BenchmarkCategory {
  name: string;
  /** Suite names without the Nightly/Weekly schedule suffix. */
  suites: string[];
}

/**
 * Suite name with the Nightly/Weekly schedule suffix stripped, so that a
 * benchmark keeps a single identity when its schedule changes (e.g.
 * CompilationBenchmarksBig36Nightly → CompilationBenchmarksBig36Weekly).
 */
export function suiteBase(suite: string): string {
  return suite.replace(/(Nightly|Weekly)$/, "");
}

export const benchmarkCategories: BenchmarkCategory[] = [
  {
    name: "Compilation – Big",
    suites: ["CompilationBenchmarksBig", "CompilationBenchmarksBig36"],
  },
  {
    name: "Compilation – Small",
    suites: ["CompilationBenchmarksSmall"],
  },
  {
    name: "Compilation – Xsbti",
    suites: ["CompilationBenchmarksXsbti"],
  },
  {
    name: "Runtime – Optimizer",
    suites: ["RuntimeBenchmarksOptimizerSmall"],
  },
  {
    name: "Runtime – Libraries",
    suites: ["RuntimeBenchmarksLibs"],
  },
  {
    name: "Runtime – Are We Fast Yet?",
    suites: ["RuntimeBenchmarksAwfy"],
  },
];
