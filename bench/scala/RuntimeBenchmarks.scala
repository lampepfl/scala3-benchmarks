package bench

import java.util.concurrent.TimeUnit.MILLISECONDS

import org.openjdk.jmh.annotations.{
  BenchmarkMode,
  Fork,
  Measurement,
  OutputTimeUnit,
  Scope,
  State,
  Warmup,
}

@Fork(value = 1, jvmArgsPrepend = Array("-Xms2G", "-Xmx2G"))
@Warmup(iterations = 30)
@Measurement(iterations = 10)
@BenchmarkMode(Array(org.openjdk.jmh.annotations.Mode.SingleShotTime))
@State(Scope.Benchmark)
@OutputTimeUnit(MILLISECONDS)
abstract class RuntimeBenchmarks
