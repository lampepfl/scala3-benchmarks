package bench

import scala.runtime.Tuples
import scala.util.Random

import org.openjdk.jmh.annotations.{Benchmark, Measurement, Warmup}
import org.openjdk.jmh.infra.Blackhole

// Tuple benchmarks ported from the `bench-run` project of the Scala 3
// repository (https://github.com/scala/scala3/tree/main/bench-run, Apache
// License 2.0), which is no longer built there.
//
// Upstream ran each operation once per input from the files in
// `bench-run/inputs/` (JMH `@Param`s). Results here are keyed by benchmark
// name only, so each invocation instead runs the operation on all the inputs
// of that file. The upstream baselines on arrays (`arrayMap`, `consArray`,
// `cloneArray`, ...) and `productElement` are not ported, as they do not
// measure tuples.

/** Generic tuple algorithms (upstream `TupleOps.scala`). */
object TupleOps:
  def tupleFlatMap(tuple: Tuple, f: [A] => A => Tuple): Tuple =
    def tailRecFlatMap(t: Tuple, acc: Tuple): Tuple = t match
      case Tuple() => acc
      case x *: rest => tailRecFlatMap(rest, acc ++ f(x))
    tailRecFlatMap(tuple, Tuple())

  def tupleReverse(tuple: Tuple): Tuple =
    def tailRecReverse(t: Tuple, acc: Tuple): Tuple = t match
      case Tuple() => acc
      case x *: rest => tailRecReverse(rest, x *: acc)
    tailRecReverse(tuple, Tuple())

  def tupleMerge(tuple1: Tuple, tuple2: Tuple): Tuple = (tuple1, tuple2) match
    case (_, Tuple()) => tuple1
    case (Tuple(), _) => tuple2
    case (x *: xs, y *: ys) =>
      if x.asInstanceOf[Int] <= y.asInstanceOf[Int] then x *: tupleMerge(xs, tuple2)
      else y *: tupleMerge(tuple1, ys)

  def tupleMergeSort(tuple: Tuple): Tuple =
    if tuple.size <= 1 then tuple
    else
      val (tuple1, tuple2) = tuple.splitAt(tuple.size / 2): (Tuple, Tuple)
      val (sorted1, sorted2) = (tupleMergeSort(tuple1), tupleMergeSort(tuple2))
      tupleMerge(sorted1, sorted2)

object TupleBenchmarksData:
  type Id[X] = X

  def tupleOfSize(n: Int, elem: String = "elem"): Tuple =
    var t: Tuple = Tuple()
    for _ <- 1 to n do t = elem *: t
    t

// One invocation takes between 0.3 and 25 µs (tupleApply aside), so batches
// of 10000 give iterations of 3 to 250 ms.
@Warmup(batchSize = 10000, iterations = 20)
@Measurement(batchSize = 10000, iterations = 10)
class RuntimeBenchmarksTuplesWeekly extends RuntimeBenchmarks:
  import TupleBenchmarksData.*
  import TupleOps.*

  /** `tuples(n)` has size `n`, for `n` from 0 to 50. */
  private val tuples: Array[Tuple] = Array.tabulate(51)(tupleOfSize(_))
  // Entries at index 0 are unused.
  private val nonEmptyTuples: Array[NonEmptyTuple] = tuples.map(_.asInstanceOf[NonEmptyTuple])
  private val products: Array[Product] = tuples.map(_.asInstanceOf[Product])
  private val arrays: Array[Array[Object]] = Array.tabulate(51)(Array.fill[Object](_)("elem"))
  private val iarrays: Array[IArray[Object]] = Array.tabulate(51)(IArray.fill[Object](_)("elem"))
  private val elTuples: Array[Tuple] = Array.tabulate(51)(tupleOfSize(_, "el"))
  private val emTuples: Array[Tuple] = Array.tabulate(51)(tupleOfSize(_, "em"))
  private val tuple15: Tuple = tupleOfSize(15)
  private val tuple20: NonEmptyTuple = nonEmptyTuples(20)

  // Upstream `TupleOps` inputs.
  private val tupleOps1: Tuple = Tuple.fromArray(Array.tabulate(14)(i => s"elem${14 - i}"))
  private val tupleOps2: Tuple = Tuple.fromArray(Array.tabulate(9)(i => s"elem${9 - i}"))
  private val tupleOps3: Tuple = Tuple.fromArray(Random(12345).shuffle(1 to 15).toArray)

  /** Runs `op(n)` for `n` from `from` to `to` (inclusive), consuming each
   *  result.
   */
  private inline def loop(from: Int, to: Int, bh: Blackhole)(inline op: Int => Any): Unit =
    var n = from
    while n <= to do
      bh.consume(op(n))
      n += 1

  // inputs/tuples/apply.in: a tuple of size 20, indices 0 to 19. One
  // invocation takes about 60 ns, hence the larger batches.
  @Warmup(batchSize = 100000, iterations = 20)
  @Measurement(batchSize = 100000, iterations = 10)
  @Benchmark def tupleApply(bh: Blackhole): Unit =
    loop(0, 19, bh)(i => Tuples.apply(tuple20, i))

  // inputs/tuples/concat.in: sizes 15 and 0 to 50.
  @Benchmark def tupleConcat(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.concat(tuple15, tuples(n)))

  // inputs/tuples/cons.in: sizes 0 to 50.
  @Benchmark def tupleCons(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.cons("elem", tuples(n)))

  // inputs/tuples/conversions.in: sizes 1 to 50.
  @Benchmark def tupleToArray(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.toArray(tuples(n)))

  @Benchmark def tupleToIArray(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.toIArray(tuples(n)))

  @Benchmark def tupleFromArray(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.fromArray(arrays(n)))

  @Benchmark def tupleFromIArray(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.fromIArray(iarrays(n)))

  @Benchmark def productToArray(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.productToArray(products(n)))

  // inputs/tuples/map.in: sizes 0 to 50.
  @Benchmark def tupleMap(bh: Blackhole): Unit =
    loop(0, 50, bh): n =>
      Tuples.map[Id](tuples(n), [T] => (x: T) => x.asInstanceOf[String].updated(0, 'a').asInstanceOf[T])

  // inputs/tuples/tail.in: sizes 1 to 50.
  @Benchmark def tupleTail(bh: Blackhole): Unit =
    loop(1, 50, bh)(n => Tuples.tail(nonEmptyTuples(n)))

  // inputs/tuples/zip.in: sizes 0 to 50.
  @Benchmark def tupleZip(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.zip(elTuples(n), emTuples(n)))

  // inputs/drop.in, split.in, take.in: sizes 0 to 50, at half the size.
  @Benchmark def tupleDrop(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.drop(tuples(n), n / 2))

  @Benchmark def tupleSplit(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.splitAt(tuples(n), n / 2))

  @Benchmark def tupleTake(bh: Blackhole): Unit =
    loop(0, 50, bh)(n => Tuples.take(tuples(n), n / 2))

  // TupleOps: compiled user code on top of the tuple operations.
  @Benchmark def reverse: Tuple =
    tupleReverse(tupleOps1)

  @Benchmark def flatMap: Tuple =
    tupleFlatMap(tupleOps2, [A] => (x: A) => (x, x))

  @Benchmark def mergeSort: Tuple =
    tupleMergeSort(tupleOps3)
