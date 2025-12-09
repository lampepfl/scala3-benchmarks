package bench

import org.openjdk.jmh.annotations.Benchmark

abstract class CompilationBenchmarksBig extends CompilationBenchmarks:
  @Benchmark def dottyUtils = scalac(Config.dottyUtil)
  @Benchmark def fansi = scalac(Config.fansi)
  @Benchmark def re2s = scalac(Config.re2s)
  @Benchmark def scalaParserCombinators = scalac(Config.scalaParserCombinators)
  @Benchmark def scalaYaml = scalac(Config.scalaYaml)
  @Benchmark def sourcecode = scalac(Config.sourcecode)
  //@Benchmark def stdlib123 = scalac(Config.stdlib213)
