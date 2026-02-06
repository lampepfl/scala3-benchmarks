package bench

import org.openjdk.jmh.annotations.{Benchmark, Measurement, Warmup}
import clbg.*
import kostya.*

class RuntimeBenchmarksOptimizerSmallNightly extends RuntimeBenchmarks:

  @Benchmark
  def ary: Unit =
    assert(Ary.main() == 1000000)

  @Benchmark
  def ary2: Unit =
    assert(Ary2.main() == 1000000)

  @Benchmark
  def matMul: Unit =
    assert(MatMul.main() == -18.6716666)

  @Benchmark
  def meteor: Unit =
    assert(Meteor.main() == "9 9 9 9 8 \n 9 6 6 8 5 \n6 6 8 8 5 \n 6 8 2 5 5 \n7 7 7 2 5 \n 7 4 7 2 0 \n1 4 2 2 0 \n 1 4 4 0 3 \n1 4 0 0 3 \n 1 1 3 3 3 ")

  @Benchmark
  def nestedLoop: Unit =
    assert(NestedLoop.main() == 1000000)

  @Benchmark
  def nestedLoop2: Unit =
    assert(NestedLoop2.main() == 1000000)
