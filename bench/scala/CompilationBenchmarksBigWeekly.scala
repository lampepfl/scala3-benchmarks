package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.{Benchmark, Warmup}

class CompilationBenchmarksBigWeekly extends CompilationBenchmarks:

  @Warmup(iterations = 100)
  @Benchmark
  def scalaParserCombinators =
    assert(Config.scalaParserCombinators.sources.size == 50)
    DottyCompiler.compile(Config.scalaParserCombinators.sources, Config.scalaParserCombinators.options, outDir)

  @Warmup(iterations = 60)
  @Benchmark
  def scalaz =
    assert(Config.scalaz.sources.size == 292)
    DottyCompiler.compile(Config.scalaz.sources, Config.scalaz.options, outDir)
