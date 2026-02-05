package bench

import bench.compilers.DottyCompiler
import org.openjdk.jmh.annotations.Benchmark

class CompilationBenchmarksSmallNightly extends CompilationBenchmarks:

  @Benchmark def helloWorld =
    assert(Config.helloWorld.sources.size == 1)
    DottyCompiler.compile(Config.helloWorld.sources, Config.helloWorld.options, outDir)

  @Benchmark def implicitInductive =
    assert(Config.implicitInductive.sources.size == 1)
    DottyCompiler.compile(Config.implicitInductive.sources, Config.implicitInductive.options, outDir)

  @Benchmark def matchTypeSort =
    assert(Config.matchTypeSort.sources.size == 1)
    DottyCompiler.compile(Config.matchTypeSort.sources, Config.matchTypeSort.options, outDir)

  @Benchmark def patmatexhaust =
    assert(Config.patmatexhaust.sources.size == 1)
    DottyCompiler.compile(Config.patmatexhaust.sources, Config.patmatexhaust.options, outDir)

