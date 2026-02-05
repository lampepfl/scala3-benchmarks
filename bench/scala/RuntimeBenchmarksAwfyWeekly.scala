package bench

import org.openjdk.jmh.annotations.Benchmark

import bounce.BounceBenchmark
import brainfuck.BrainfuckBenchmark
import cd.CDBenchmark
import deltablue.DeltaBlue
import gcbench.GCBenchBenchmark
import json.JsonBenchmark
import kmeans.KmeansBenchmark
import list.ListBenchmark
import mandelbrot.MandelbrotBenchmark
import nbody.NbodyBenchmark
import permute.PermuteBenchmark
import queens.QueensBenchmark
import richards.Richards
import tracer.Tracer

class RuntimeBenchmarksAwfyWeekly extends RuntimeBenchmarks:

  @Benchmark def bounce: Unit =
    assert(BounceBenchmark.run(100) == 1331)

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
    assert(ListBenchmark.run(5) == 10)

  @Benchmark def mandelbrot: Unit =
    assert(MandelbrotBenchmark.run(750) == 50)

  @Benchmark def nbody: Unit =
    assert(NbodyBenchmark.run(250000))

  @Benchmark def queens: Unit =
    assert(QueensBenchmark.run())

  @Benchmark def permute: Unit =
    assert(PermuteBenchmark.run(6) == 720)

  @Benchmark def richards: Unit =
    Richards.run()

  @Benchmark def tracer: Unit =
    Tracer.run()
