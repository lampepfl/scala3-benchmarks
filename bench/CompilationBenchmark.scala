package bench

import java.util.concurrent.TimeUnit.MILLISECONDS

import scala.sys.process.{ProcessBuilder, stringToProcess}

import dotty.tools.dotc.{Compiler, Driver, Run}
import dotty.tools.dotc.core.Contexts.{ctx, withMode, Context, ContextBase}
import dotty.tools.dotc.core.Mode
import dotty.tools.dotc.core.Types.{TermRef, Type}
import dotty.tools.io.{ClassPath, VirtualDirectory}

import org.openjdk.jmh.annotations.{
  Benchmark,
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

@Fork(value = 1, jvmArgsPrepend = Array("-XX:+PrintCommandLineFlags", "-Xms8G", "-Xmx8G"))
@Warmup(iterations = 0)
@Measurement(iterations = 180)
@BenchmarkMode(Array(org.openjdk.jmh.annotations.Mode.SingleShotTime))
@State(Scope.Benchmark)
@OutputTimeUnit(MILLISECONDS)
class CompilationBenchmarks:
  val outDir = "out"

  // Preloaded classpath (shared across all benchmarks since they use the same classpath)
  var preloadedClasspath: ClassPath = null
  var outVirtualDir: VirtualDirectory = null

  @Setup(Level.Trial)
  def setupTrial(): Unit =
    // All benchmarks share the same classpath, so we can use any of them
    preloadedClasspath = PreloadedClasspath.preloadClasspath(Config.helloWorldClasspath)
    outVirtualDir = new VirtualDirectory("out", None)

  @Setup(Level.Iteration)
  def setupIteration(): Unit =
    outVirtualDir.clear()

  /** Launches `scalac` with the given arguments and preloaded classpath. */
  def scalac(args: Seq[String]) =
    val allArgs = Array("-Werror") ++ args
    val reporter = BenchmarkDriver(preloadedClasspath, outVirtualDir).process(allArgs)
    assert(!reporter.hasErrors, "Compilation failed with errors")

  @Benchmark def helloWorld = scalac(Config.helloWorldArgs)
  @Benchmark def matchTypeBubbleSort = scalac(Config.matchTypeBubbleSortArgs)

  // Library benchmarks
  @Benchmark def dottyUtils = scalac(Config.dottyUtilArgs)
  @Benchmark def re2s = scalac(Config.re2sArgs)
  @Benchmark def scalaParserCombinators = scalac(Config.scalaParserCombinatorsArgs)
  @Benchmark def scalaYaml = scalac(Config.scalaYamlArgs)
  @Benchmark def sourcecode = scalac(Config.sourcecodeArgs)

  // Benchmarks from previous suite
  @Benchmark def exhaustivityI = scalac(Config.exhaustivityIArgs)
  @Benchmark def exhaustivityS = scalac(Config.exhaustivitySArgs)
  @Benchmark def exhaustivityT = scalac(Config.exhaustivityTArgs)
  @Benchmark def exhaustivityV = scalac(Config.exhaustivityVArgs)
  @Benchmark def findRef = scalac(Config.findRefArgs)
  @Benchmark def i1535 = scalac(Config.i1535Args)
  @Benchmark def i1687 = scalac(Config.i1687Args)
  @Benchmark def implicitCache = scalac(Config.implicitCacheArgs)
  @Benchmark def implicitInductive = scalac(Config.implicitInductiveArgs)
  @Benchmark def implicitNums = scalac(Config.implicitNumsArgs)
  @Benchmark def implicitScopeLoop = scalac(Config.implicitScopeLoopArgs)
  @Benchmark def patmatexhaust = scalac(Config.patmatexhaustArgs)
  @Benchmark def tuple = scalac(Config.tupleArgs)
  @Benchmark def tuple22Apply = scalac(Config.tuple22ApplyArgs)
  @Benchmark def tuple22Cons = scalac(Config.tuple22ConsArgs)
  @Benchmark def tuple22Creation = scalac(Config.tuple22CreationArgs)
  @Benchmark def tuple22Size = scalac(Config.tuple22SizeArgs)
  @Benchmark def tuple22Tails = scalac(Config.tuple22TailsArgs)
