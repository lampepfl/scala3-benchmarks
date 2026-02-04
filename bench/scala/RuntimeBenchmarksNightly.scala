package bench

import org.openjdk.jmh.annotations.Benchmark

import queens.QueensBenchmark
import deltablue.DeltaBlue
import richards.Richards
import nbody.NbodyBenchmark
import mandelbrot.MandelbrotBenchmark

class RuntimeBenchmarksNightly extends RuntimeBenchmarks:

  @Benchmark def queens: Unit =
    assert(QueensBenchmark.run())

  @Benchmark def deltablue: Unit =
    DeltaBlue.run()

  @Benchmark def richards: Unit =
    Richards.run()

  @Benchmark def nbody: Unit =
    assert(NbodyBenchmark.run(250000))

  @Benchmark def mandelbrot: Unit =
    assert(MandelbrotBenchmark.run(750) == 50)
