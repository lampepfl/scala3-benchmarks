# Scala 3 Compiler Benchmarks

JMH benchmarks for measuring Scala 3 compiler performance.

## Benchmarks

**Real-world code** (multi-file): Sources are vendored (copied directly into this repository) and fixed to compile without errors or warnings across all Scala versions from 3.3.7 to nightly. Fixed versions ensure comparable benchmark results.

| Project | Version | LOC | Dependencies | Tests |
|---------|---------|----:|--------------|-------|
| [cask app](https://www.lihaoyi.com/post/SimpleWebandApiServerswithScala.html) | - | 115 | cask, scalatags | no |
| [dotty util](https://github.com/scala/scala3/tree/main/compiler/src/dotty/tools/dotc/util) | 6462d7d7 | 2'209 | none | no |
| [fansi](https://github.com/com-lihaoyi/fansi) | 0.5.1 | 960 | sourcecode, utest | yes |
| [indigo](https://github.com/PurpleKingdomGames/indigoengine) ⚡ | 0.22.0 | 25'270 | scalajs-dom, ultraviolet | no |
| [re2s](https://github.com/twitter/rsc/tree/1d2b8962604206b1328e94257885117fd252bd23/examples/re2s/src/main/scala/java/util/regex) | 1d2b8962 | 9'021 | none | no |
| [scala-parallel-collections](https://github.com/scala/scala-parallel-collections) | v1.2.0 | 8'887 | junit | yes |
| [scala-parser-combinators](https://github.com/scala/scala-parser-combinators) | 2.4.0 | 2'325 | junit | yes |
| [scala.today](https://github.com/VirtusLab/scala.today) | 2dd97e7 | 1'103 | tapir, ox, magnum, etc. | no |
| [scala-yaml](https://github.com/VirtusLab/scala-yaml) | 0.3.1 | 6'473 | pprint, munit | yes |
| [scalaz](https://github.com/scalaz/scalaz) | v7.2.36 | 27'757| none | no |
| [sourcecode](https://github.com/com-lihaoyi/sourcecode) | 0.4.4 | 638 | none | yes |
| [tasty-query](https://github.com/scalacenter/tasty-query) | v1.6.1 | 13'482 | none | no |
| [tictactoe](https://github.com/katlasik/scala3-tictactoe) | 6873dfd | 441 | cats-effect, cats-core | yes |

⚡ = Scala.js benchmark (requires Scala 3.6.4+)  
LOC = lines of Scala code (reported by [cloc](https://github.com/AlDanial/cloc))

**Synthetic benchmarks** (single-file): The remaining benchmarks target specific compiler aspects (pattern matching, implicit resolution, inlining, etc.). Most are adapted from the previous benchmark suite.

## Quick Start

```bash
# Run benchmarks for multiple versions with interleaved runs
./run.sh --versions 3.3.4 3.7.4 --jvm temurin:21 --runs 3

# Or run manually with sbt
sbt -Dcompiler.version=3.3.4 "clean; bench / Jmh / run -gc true -foe true"
```

## Structure of this Repository

```
bench-sources/
  small/                # Synthetic single-file benchmarks
    helloWorld.scala
    ...
  dottyUtil/            # Real-world multi-file benchmarks (each is an SBT subproject)
  ...
bench/scala/
  CompilationBenchmarksSmallNightly.scala
  ...
```

## Results structure

Results are stored as CSV files in the [scala3-benchmarks-data](https://github.com/lampepfl/scala3-benchmarks-data) repository. The [scripts/importResults.scala](scripts/importResults.scala) script converts JMH JSON output into two forms: raw data (one file per run) and aggregated summaries (one file per metric/benchmark pair, for use by the visualizer).

### Raw Data

`raw/<machine>/<jvm>/<patch_version>/<version>/<run_datetime>.csv`

Each CSV file contains one row per benchmark from a single JMH run. An `INDEX` file in each leaf directory lists all run files.

Columns:

- `benchmark`: unqualified `@Benchmark` method name (e.g. `helloWorld`).
- `warmup_iterations`: number of warmup iterations before measurement.
- `times`: space-separated measurement times in milliseconds (one per iteration). The number of values is the number of measurement iterations. Benchmarks use `SingleShotTime` mode, so each value is a single invocation. See [JMH @BenchmarkMode](https://javadoc.io/doc/org.openjdk.jmh/jmh-core/latest/org/openjdk/jmh/annotations/BenchmarkMode.html).
- `allocs_min`, `allocs_avg`, `allocs_max`: total allocation per operation in MB, from the `gc.alloc.rate.norm` secondary metric of JMH's [`-prof gc` (GcProfiler)](https://github.com/Valloric/jmh-playground/blob/d91560bfea6c18cd065b4bf9be9e2da14864cbf1/src/jmh/java/org/openjdk/jmh/samples/JMHSample_35_Profilers.java#L170-L212). The raw value (bytes) is divided by 1e6.
- `gc_min`, `gc_avg`, `gc_max`: number of GC events during measurement, from the `gc.count` secondary metric of `-prof gc`.
- `comp_min`, `comp_avg`, `comp_max`: JVM JIT compilation time in milliseconds during the measurement window, from the `compiler.time.profiled` secondary metric of [`-prof comp` (CompilerProfiler)](https://github.com/Valloric/jmh-playground/blob/d91560bfea6c18cd065b4bf9be9e2da14864cbf1/src/jmh/java/org/openjdk/jmh/samples/JMHSample_35_Profilers.java#L306-L318). High values indicate the JIT was still active during measurement, which can interfere with results. In practice this represents 10-20% of total measured time. Its reliability is uncertain.

### Aggregated Data

`aggregated/<machine>/<jvm>/<patch_version>/<metric>/<benchmark>.csv`

Pre-computed summaries derived from raw data, organized per metric and benchmark for direct use by the visualiser. Each `<metric>` is one of `time`, `allocs`, `gc`, or `comp`.

Columns:

- `version`: Scala version string.
- `count`: total number of measurement iterations across all runs for this version.
- `min`: minimum value across all iterations.
- `avg`: weighted average across all iterations.
- `max`: maximum value across all iterations.

When multiple runs exist for the same version, stats are merged incrementally (combined average weighted by count, min/max taken across all runs).


## Adding Benchmarks

Benchmarks should:

- compile with all version between Scala 3.3.2 and the latest
- compile in ~100ms-10s range (after warmup)
- not require complex setup
- Ideally not require external dependencies

Potential future benchmarks:

- [quicklens](https://github.com/softwaremill/quicklens)  (waiting 3.8)
- advent of code solutions (various authors, various sizes)


To add a new benchmark:

1. Add a `.scala` file to `bench-sources/small/`, or create a new SBT subproject in `bench-sources/` for multi-file benchmarks
2. Add a `@Benchmark` method in `CompilationBenchmarks.scala`:
   ```scala
   @Benchmark def myBenchmark = scalac(Config.myBenchmark)
   ```

`Config` is auto-generated at `bench/target/scala-*/src_managed/main/bench/Config.scala` with the `scalac` arguments (classpath and sources) for each benchmark.

## Running Tests

Some benchmarks (fansi, sourcecode, scalaYaml, parserCombinators) include tests from their upstream repositories:

```bash
sbt test                    # Run all tests
sbt benchFansi/test         # Run fansi tests only
```

Test sources are also included in benchmarks to compile both main and test code together.

## Using JMH's profilers

Examples of using JMH's built-in profilers: [jmh/samples/JMHSample_35_Profilers.java](https://github.com/openjdk/jmh/blob/master/jmh-samples/src/main/java/org/openjdk/jmh/samples/JMHSample_35_Profilers.java).

### Async Profiler

Flame graphs can be generated using [async-profiler](https://github.com/async-profiler/async-profiler). Example command:

```bash
sbt -Dcompiler.version=3.7.4 "clean; bench / Jmh / run -gc true -foe true -prof \"async:libPath=../async-profiler-4.2.1-macos/lib/libasyncProfiler.dylib;output=flamegraph;dir=profile-results;include=CompilationBenchmarks.scalac\" helloWorld"
```

Replace `3.7.4`, `../async-profiler-4.2.1-macos/lib/libasyncProfiler.dylib` and `helloWorld` with the desired Scala version, path to the async profiler library, and benchmark name respectively. Read more at [markrmiller/jmh-profilers.md](https://gist.github.com/markrmiller/a04f5c734fad879f688123bc312c21af#using-jmh-with-the-async-profiler).

The default sampling interval is 10ms. It can be changed by adding the `interval`, which is specified in nanoseconds. For example, to set the interval to 1ms, use `interval=1000000`.

Async-profiler options reference [async-profiler/docs/ProfilerOptions.md](https://github.com/async-profiler/async-profiler/blob/master/docs/ProfilerOptions.md).

## Known Issues

Under Java 25, the following warning is printed during benchmark runs:

```
[info] WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
[info] WARNING: sun.misc.Unsafe::objectFieldOffset has been called by org.openjdk.jmh.util.Utils (file:/home/runner/work/scala3-benchmarks/scala3-benchmarks/target/bg-jobs/sbt_accfab51/target/09a4797f/1296d6b9/jmh-core-1.37.jar)
[info] WARNING: Please consider reporting this to the maintainers of class org.openjdk.jmh.util.Utils
[info] WARNING: sun.misc.Unsafe::objectFieldOffset will be removed in a future release
```

It can be ignored for now. It is fixed by https://github.com/openjdk/jmh/pull/140, which will be included in the next JMH release.
