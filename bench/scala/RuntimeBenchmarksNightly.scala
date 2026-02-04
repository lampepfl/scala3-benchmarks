package bench

import org.openjdk.jmh.annotations.Benchmark

import queens.QueensBenchmark
import deltablue.DeltaBlue
import richards.Richards
import nbody.NbodyBenchmark
import mandelbrot.MandelbrotBenchmark
import bounce.BounceBenchmark
import brainfuck.BrainfuckBenchmark
import cd.CDBenchmark

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

  @Benchmark def bounce: Unit =
    assert(BounceBenchmark.run(100) == 1331)

  @Benchmark def brainfuck: Unit =
    assert(BrainfuckBenchmark.run() == 11359)

  @Benchmark def cd: Unit =
    assert(CDBenchmark.run(100) == 4305)
