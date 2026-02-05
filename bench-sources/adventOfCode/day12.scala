// Adapted from https://github.com/scalacenter/scala-advent-of-code
// 2023 Day 12: Hot Springs
package aoc.day12

/** Sums `countRow` over all rows in `input`. */
def countAll(input: String): Long = input.split("\n").map(countRow).sum

/** Counts all of the different valid arrangements of operational and broken
  * springs in the given row.
  */
def countRow(input: String): Long =
  val Array(conditions, damagedCounts) = input.split(" ")
  count2(
    conditions.toList,
    damagedCounts.split(",").map(_.toInt).toList
  )

extension (b: Boolean) private inline def toLong: Long = if b then 1L else 0L

def count2(input: List[Char], ds: List[Int]): Long =
  val dim1 = input.length + 1
  val dim2 = ds.length + 1
  val cache = Array.fill(dim1 * dim2)(-1L)

  inline def count2Cached(input: List[Char], ds: List[Int]): Long =
    val key = input.length * dim2 + ds.length
    val result = cache(key)
    if result == -1L then
      val result = count2Uncached(input, ds)
      cache(key) = result
      result
    else result

  def count2Uncached(input: List[Char], ds: List[Int]): Long =
    // We've  seen all expected damaged sequences. The arrangement is therefore
    // valid only if the input does not contain damaged springs.
    if ds.isEmpty then input.forall(_ != '#').toLong
    // The input is empty but we expected some damaged springs, so this is not a
    // valid arrangement.
    else if input.isEmpty then 0L
    else
      inline def operationalCase(): Long =
        // Operational case: we can consume all operational springs to get to
        // the next choice.
        count2Cached(input.tail.dropWhile(_ == '.'), ds)
      inline def damagedCase(): Long =
        // If the length of the input is less than the expected length of the
        // damaged sequence, then this is not a valid arrangement.
        if input.length < ds.head then 0L
        else
          // Split the input into a group of length ds.head and the rest.
          val (group, rest) = input.splitAt(ds.head)
          // If the group contains any operational springs, then this is not a a
          // group of damaged springs, so this is not a valid arrangement.
          if !group.forall(_ != '.') then 0L
          // If the rest of the input is empty, then this is a valid arrangement
          // only if the damaged sequence is the last one expected.
          else if rest.isEmpty then ds.tail.isEmpty.toLong
          // If we now have a damaged spring, then this is not the end of a
          // damaged sequence as expected, and therefore not a valid
          // arrangement.
          else if rest.head == '#' then 0L
          // Otherwise, we can continue with the rest of the input and the next
          // expected damaged sequence.
          else count2Cached(rest.tail, ds.tail)
      input.head match
        case '?' => operationalCase() + damagedCase()
        case '.' => operationalCase()
        case '#' => damagedCase()

  count2Cached(input, ds)

def countAllUnfolded(input: String): Long =
  input.split("\n").map(unfoldRow).map(countRow).sum

def unfoldRow(input: String): String =
  val Array(conditions, damagedCounts) = input.split(" ")
  val conditionsUnfolded = (0 until 5).map(_ => conditions).mkString("?")
  val damagedCountUnfolded = (0 until 5).map(_ => damagedCounts).mkString(",")
  f"$conditionsUnfolded $damagedCountUnfolded"
