package bench

import org.openjdk.jmh.annotations.{Benchmark, Measurement, Warmup}

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

  @Warmup(batchSize = 1000, iterations = 10)
  @Measurement(batchSize = 1000, iterations = 10)
  @Benchmark
  def bounce: Unit =
    assert(BounceBenchmark.run(100) == 1331)

  @Warmup(batchSize = 10, iterations = 10)
  @Measurement(batchSize = 10, iterations = 10)
  @Benchmark
  def brainfuck: Unit =
    assert(BrainfuckBenchmark.run() == 11359)

  @Benchmark
  def cd: Unit =
    assert(CDBenchmark.run(100) == 4305)

  @Warmup(batchSize = 100, iterations = 10)
  @Measurement(batchSize = 100, iterations = 10)
  @Benchmark
  def deltablue: Unit =
    DeltaBlue.run()

  @Benchmark
  def gcbench: Unit =
    assert(GCBenchBenchmark.run())

  @Warmup(batchSize = 10, iterations = 10)
  @Measurement(batchSize = 10, iterations = 10)
  @Benchmark
  def json: Unit =
    assert(JsonBenchmark.run(JsonBenchmark.input) == 156)

  @Benchmark
  def kmeans: Unit =
    assert(KmeansBenchmark.run(100000))

  @Warmup(batchSize = 1000, iterations = 10)
  @Measurement(batchSize = 1000, iterations = 10)
  @Benchmark
  def list: Unit =
    assert(ListBenchmark.run(5) == 10)

  @Benchmark
  def mandelbrot: Unit =
    assert(MandelbrotBenchmark.run(750) == 50)

  @Warmup(batchSize = 10, iterations = 10)
  @Measurement(batchSize = 10, iterations = 10)
  @Benchmark
  def nbody: Unit =
    assert(NbodyBenchmark.run(250000))

  @Warmup(batchSize = 10000, iterations = 10)
  @Measurement(batchSize = 10000, iterations = 10)
  @Benchmark
  def queens: Unit =
    assert(QueensBenchmark.run())

  @Warmup(batchSize = 1000, iterations = 10)
  @Measurement(batchSize = 1000, iterations = 10)
  @Benchmark
  def permute: Unit =
    assert(PermuteBenchmark.run(6) == 720)

  @Warmup(batchSize = 1000, iterations = 10)
  @Measurement(batchSize = 1000, iterations = 10)
  @Benchmark
  def richards: Unit =
    Richards.run()

  @Warmup(batchSize = 100, iterations = 10)
  @Measurement(batchSize = 100, iterations = 10)
  @Benchmark
  def tracer: Unit =
    Tracer.run()
