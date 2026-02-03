package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.Benchmark

class CompilationBenchmarksSmallNightly extends CompilationBenchmarks:

  @Benchmark def helloWorld =
    assert(Config.helloWorld.sources.size == 1)
    DottyCompiler.compile(Config.helloWorld.sources, Config.helloWorld.options, outDir)

  @Benchmark def matchTypeBubbleSort =
    assert(Config.matchTypeBubbleSort.sources.size == 1)
    DottyCompiler.compile(Config.matchTypeBubbleSort.sources, Config.matchTypeBubbleSort.options, outDir)
