package bench

import org.openjdk.jmh.annotations.Benchmark

class RuntimeBenchmarksNightly extends RuntimeBenchmarks:

  @Benchmark def queens =
    _root_.queens.QueensBenchmark.run()

  @Benchmark def deltablue =
    _root_.deltablue.DeltaBlue.run()

  @Benchmark def richards =
    _root_.richards.Richards.run()

  @Benchmark def nbody =
    _root_.nbody.NbodyBenchmark.run(250000)

  @Benchmark def mandelbrot =
    _root_.mandelbrot.MandelbrotBenchmark.run(750)
