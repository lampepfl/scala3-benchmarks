package permute

object PermuteBenchmark {
  def run(size: Int): Int = {
    val permIter = (0 until size).toList.permutations

    var count = 0
    while (permIter.hasNext) {
      permIter.next()
      count += 1
    }
    count
  }
}
