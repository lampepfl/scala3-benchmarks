package bench

import java.util.concurrent.TimeUnit.MILLISECONDS

import scala.sys.process.stringToProcess

import org.openjdk.jmh.annotations.{
  BenchmarkMode,
  Fork,
  Level,
  Measurement,
  OutputTimeUnit,
  Scope,
  Setup,
  State,
  Warmup,
}

@Fork(
  value = 1,
  jvmArgsPrepend = Array("-XX:+PrintCommandLineFlags", "-Xms8G", "-Xmx8G", "--sun-misc-unsafe-memory-access=allow"),
)
@Warmup(iterations = 150)
@Measurement(iterations = 10)
@BenchmarkMode(Array(org.openjdk.jmh.annotations.Mode.SingleShotTime))
@State(Scope.Benchmark)
@OutputTimeUnit(MILLISECONDS)
abstract class CompilationBenchmarks:

  val outDir = "out"

  /** Whether compilations write to `outDir`. If not (`BENCH_OUTPUT=memory`), the per-iteration reset is skipped. */
  protected def writesOutput: Boolean = !bench.compilers.DottyCompiler.inMemory

  @Setup(Level.Iteration)
  def setup(): Unit =
    if writesOutput then removeAndCreateDir(outDir)

  /** Removes and creates a directory. */
  def removeAndCreateDir(dir: String) =
    // Using `rm` instead of Java's API because it is better at removing the
    // whole directory atomically. Got occasional `DirectoryNotEmptyException`
    // exceptions with the Java's API.
    s"rm -rf $dir".!
    s"mkdir -p $dir".!
