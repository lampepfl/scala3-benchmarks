package bench

import org.openjdk.jmh.annotations.Benchmark

class CompilationBenchmarksSmall extends CompilationBenchmarks:
  @Benchmark def exhaustivityI = scalac(Config.exhaustivityI)
  @Benchmark def exhaustivityS = scalac(Config.exhaustivityS)
  @Benchmark def exhaustivityT = scalac(Config.exhaustivityT)
  @Benchmark def exhaustivityV = scalac(Config.exhaustivityV)
  @Benchmark def findRef = scalac(Config.findRef)
  @Benchmark def helloWorld = scalac(Config.helloWorld)
  @Benchmark def i1535 = scalac(Config.i1535)
  @Benchmark def i1687 = scalac(Config.i1687)
  @Benchmark def implicitCache = scalac(Config.implicitCache)
  @Benchmark def implicitInductive = scalac(Config.implicitInductive)
  @Benchmark def implicitNums = scalac(Config.implicitNums)
  @Benchmark def implicitScopeLoop = scalac(Config.implicitScopeLoop)
  @Benchmark def matchTypeBubbleSort = scalac(Config.matchTypeBubbleSort)
  @Benchmark def patmatexhaust = scalac(Config.patmatexhaust)
  @Benchmark def tuple = scalac(Config.tuple)
  @Benchmark def tuple22Apply = scalac(Config.tuple22Apply)
  @Benchmark def tuple22Cons = scalac(Config.tuple22Cons)
  @Benchmark def tuple22Creation = scalac(Config.tuple22Creation)
  @Benchmark def tuple22Size = scalac(Config.tuple22Size)
  @Benchmark def tuple22Tails = scalac(Config.tuple22Tails)
