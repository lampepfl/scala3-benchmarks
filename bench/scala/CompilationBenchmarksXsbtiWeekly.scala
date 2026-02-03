package bench

import bench.compilers.XsbtiCompiler
import org.openjdk.jmh.annotations.{Benchmark, Warmup}

class CompilationBenchmarksXsbtiWeekly extends CompilationBenchmarks:

  @Benchmark def xsbtiHelloWorld =
    assert(Config.helloWorld.sources.size == 1)
    XsbtiCompiler.compile(Config.helloWorld.sources, Config.helloWorld.options, outDir)

  @Warmup(iterations = 60)
  @Benchmark
  def xsbtiTastyQuery =
    assert(Config.tastyQuery.sources.size == 49)
    XsbtiCompiler.compile(Config.tastyQuery.sources, Config.tastyQuery.options, outDir)
