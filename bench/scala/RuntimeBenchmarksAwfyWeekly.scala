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
import gcbench.GCBenchBenchmark
import json.JsonBenchmark
import list.ListBenchmark
import permute.PermuteBenchmark
import kmeans.KmeansBenchmark
import org.scalajs.benchmark.tracer.Tracer

class RuntimeBenchmarksAwfyWeekly extends RuntimeBenchmarks:

  @Benchmark def bounce: Unit =
    assert(BounceBenchmark.run(100000) == 1278008)

  @Benchmark def brainfuck: Unit =
    assert(BrainfuckBenchmark.run() == 11359)

  @Benchmark def cd: Unit =
    assert(CDBenchmark.run(100) == 4305)

  @Benchmark def deltablue: Unit =
    DeltaBlue.run()

  @Benchmark def gcbench: Unit =
    assert(GCBenchBenchmark.run())

  @Benchmark def json: Unit =
    assert(JsonBenchmark.run(JsonBenchmark.input) == 156)

  @Benchmark def kmeans: Unit =
    assert(KmeansBenchmark.run(100000))

  @Benchmark def list: Unit =
    assert(ListBenchmark.run(9) == 18)

  @Benchmark def mandelbrot: Unit =
    assert(MandelbrotBenchmark.run(750) == 50)

  @Benchmark def nbody: Unit =
    assert(NbodyBenchmark.run(250000))

  @Benchmark def queens: Unit =
    assert(QueensBenchmark.run())

  @Benchmark def permute: Unit =
    assert(PermuteBenchmark.run(10) == 3628800)

  @Benchmark def richards: Unit =
    Richards.run()

  @Benchmark def tracer: Unit =
    Tracer.run()
