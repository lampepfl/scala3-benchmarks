package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.{Benchmark, Warmup}

class CompilationBenchmarksBigNightly extends CompilationBenchmarks:

  @Benchmark
  def caskApp =
    assert(Config.caskApp.sources.size == 1)
    DottyCompiler.compile(Config.caskApp.sources, Config.caskApp.options, outDir)

  @Warmup(iterations = 170)
  @Benchmark
  def dottyUtils =
    assert(Config.dottyUtil.sources.size == 34)
    DottyCompiler.compile(Config.dottyUtil.sources, Config.dottyUtil.options, outDir)

  @Benchmark
  def fansi =
    assert(Config.fansi.sources.size == 2)
    DottyCompiler.compile(Config.fansi.sources, Config.fansi.options, outDir)

  @Warmup(iterations = 120)
  @Benchmark
  def re2s =
    assert(Config.re2s.sources.size == 17)
    DottyCompiler.compile(Config.re2s.sources, Config.re2s.options, outDir)

  @Warmup(iterations = 100)
  @Benchmark
  def parallelCollections =
    assert(Config.parallelCollections.sources.size == 86)
    DottyCompiler.compile(Config.parallelCollections.sources, Config.parallelCollections.options, outDir)

  @Benchmark
  def scalaToday =
    assert(Config.scalaToday.sources.size == 9)
    DottyCompiler.compile(Config.scalaToday.sources, Config.scalaToday.options, outDir)

  @Warmup(iterations = 80)
  @Benchmark
  def scalaYaml =
    assert(Config.scalaYaml.sources.size == 57)
    DottyCompiler.compile(Config.scalaYaml.sources, Config.scalaYaml.options, outDir)

  @Warmup(iterations = 170)
  @Benchmark
  def sourcecode =
    assert(Config.sourcecode.sources.size == 20)
    DottyCompiler.compile(Config.sourcecode.sources, Config.sourcecode.options, outDir)

  @Warmup(iterations = 60)
  @Benchmark
  def tastyQuery =
    assert(Config.tastyQuery.sources.size == 49)
    DottyCompiler.compile(Config.tastyQuery.sources, Config.tastyQuery.options, outDir)

  @Benchmark
  def tictactoe =
    assert(Config.tictactoe.sources.size == 16)
    DottyCompiler.compile(Config.tictactoe.sources, Config.tictactoe.options, outDir)
