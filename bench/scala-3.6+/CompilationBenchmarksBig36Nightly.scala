package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.{Benchmark, Warmup}

class CompilationBenchmarksBig36Nightly extends CompilationBenchmarks:
  
  @Warmup(iterations = 60)
  @Benchmark
  def indigo =
    assert(Config.indigo.sources.size == 223)
    DottyCompiler.compile(Config.indigo.sources, Config.indigo.options, outDir)
