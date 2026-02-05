package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.{Benchmark, Warmup}

class CompilationBenchmarksBigWeekly extends CompilationBenchmarks:

  @Warmup(iterations = 100)
  @Benchmark
  def parserCombinators =
    assert(Config.parserCombinators.sources.size == 50)
    DottyCompiler.compile(Config.parserCombinators.sources, Config.parserCombinators.options, outDir)

  @Benchmark
  def areWeFastYet =
    assert(Config.areWeFastYet.sources.size == 44)
    DottyCompiler.compile(Config.areWeFastYet.sources, Config.areWeFastYet.options, outDir)

  @Warmup(iterations = 60)
  @Benchmark
  def scalaz =
    assert(Config.scalaz.sources.size == 292)
    DottyCompiler.compile(Config.scalaz.sources, Config.scalaz.options, outDir)
