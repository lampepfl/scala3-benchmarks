package bench

import org.openjdk.jmh.annotations.{Benchmark, Warmup}

// --- Synthetic input generators ---

object AocBenchmarkData:
  // day01: Lines with mixed digits and spelled-out numbers
  val day01Input: String =
    val words = Array("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    val sb = new StringBuilder
    var i = 0
    while i < 20000 do
      // Mix of: leading text, spelled digit, middle text, numeric digit, trailing text
      val w1 = words(i % 9)
      val w2 = words((i * 3 + 2) % 9)
      val d = (i % 9) + 1
      val filler = "abcxyz".substring(0, i % 5)
      sb.append(s"$filler$w1${filler}abc${d}def$w2$filler\n")
      i += 1
    sb.toString

  // day08: 3D coordinates forming 3 clusters
  val day08Input: String =
    val sb = new StringBuilder
    var i = 0
    while i < 200 do
      // 3 clusters around (0,0,0), (1000,1000,1000), (2000,2000,2000)
      val cluster = i % 3
      val cx = cluster * 1000L
      val cy = cluster * 1000L
      val cz = cluster * 1000L
      val dx = (i * 37 + 13) % 100
      val dy = (i * 53 + 7) % 100
      val dz = (i * 71 + 3) % 100
      sb.append(s"${cx + dx},${cy + dy},${cz + dz}\n")
      i += 1
    sb.toString

  // day10: Pipe maze with rectangular loop and enclosed tiles
  val day10Input: String =
    val size = 500
    val sb = new StringBuilder
    // Row 0: S followed by dashes, ending with 7
    sb.append('S')
    var j = 1
    while j < size - 1 do
      sb.append('-')
      j += 1
    sb.append("7\n")
    // Middle rows: | dots |
    var i = 1
    while i < size - 1 do
      sb.append('|')
      j = 1
      while j < size - 1 do
        sb.append('.')
        j += 1
      sb.append("|\n")
      i += 1
    // Last row: L followed by dashes, ending with J
    sb.append('L')
    j = 1
    while j < size - 1 do
      sb.append('-')
      j += 1
    sb.append("J\n")
    sb.toString

  // day11: Galaxy grid with scattered '#' symbols
  val day11Input: Seq[String] =
    val size = 500
    val lines = Array.fill(size)(Array.fill(size)('.'))
    // Place ~300 galaxies at pseudo-random positions
    var i = 0
    while i < 500 do
      val r = (i * 127 + 31) % size
      val c = (i * 211 + 53) % size
      lines(r)(c) = '#'
      i += 1
    lines.map(_.mkString).toSeq

  // day12: Spring arrangement rows (unfolded 5x in part2)
  val day12Input: String =
    val patterns = Array(
      "???.### 1,1,3",
      ".??..??...?##. 1,1,3",
      "?#?#?#?#?#?#?#? 1,3,1,6",
      "????.#...#... 4,1,1",
      "????.######..#####. 1,6,5",
      "?##?..?##? 3,3",
      "?###???????? 3,2,1",
      "??#???#?? 1,1,2",
      ".??.??.?##. 1,1,3",
      "?#.?#.?#.?# 1,1,1,1",
    )
    val sb = new StringBuilder
    var i = 0
    // Repeat patterns to get ~1000 rows
    while i < 1000 do
      sb.append(patterns(i % patterns.length))
      sb.append('\n')
      i += 1
    sb.toString.trim

  // day13: Patterns of '#' and '.' with near-reflections (1 smudge)
  val day13Input: Seq[String] =
    val result = scala.collection.mutable.ArrayBuffer.empty[String]
    var p = 0
    while p < 2000 do
      val height = 7 + (p % 6) // 7-12 rows
      val width = 9 + (p % 5)  // 9-13 cols
      val reflectRow = 1 + (p % (height - 1)) // split point
      // Step 1: generate all rows with pseudo-random tiles
      val rows = Array.ofDim[String](height)
      var i = 0
      while i < height do
        val sb = new StringBuilder
        var j = 0
        while j < width do
          sb.append(if (i * 31 + j * 17 + p * 7) % 3 == 0 then '#' else '.')
          j += 1
        rows(i) = sb.toString
        i += 1
      // Step 2: make mirror-symmetric around reflectRow
      val pairsCount = math.min(reflectRow, height - reflectRow)
      i = 0
      while i < pairsCount do
        rows(reflectRow + i) = rows(reflectRow - 1 - i)
        i += 1
      // Step 3: add exactly 1 smudge at the reflection boundary
      val flipCol = p % width
      val sb = new StringBuilder(rows(reflectRow))
      sb.setCharAt(flipCol, if sb.charAt(flipCol) == '#' then '.' else '#')
      rows(reflectRow) = sb.toString
      // Add rows to result
      i = 0
      while i < height do
        result += rows(i)
        i += 1
      result += "" // blank line separator
      p += 1
    result.toSeq

  // day15: Lens operations (HASHMAP)
  val day15Input: String =
    val labels = Array("rn", "cm", "qp", "pc", "ot", "ab", "zx", "fy", "gm", "kl",
      "np", "wd", "rt", "sv", "bq", "hj", "tx", "pu", "em", "dz")
    val sb = new StringBuilder
    var i = 0
    while i < 20000 do
      val label = labels(i % labels.length)
      if i % 3 == 0 then sb.append(s"$label-")
      else sb.append(s"$label=${(i % 9) + 1}")
      if i < 19999 then sb.append(',')
      i += 1
    sb.toString

  // day19: Workflows with many rules for abstract range evaluation
  val day19Input: String =
    val sb = new StringBuilder
    // Linear chain of rules: each accepts a slice and passes the rest along
    sb.append("in{x<2000:w0,A}\n")
    var i = 0
    while i < 5000 do
      val channel = "xmas".charAt(i % 4)
      val threshold = 500 + (i * 37) % 3500
      val op = if i % 2 == 0 then '<' else '>'
      val nextTarget = if i == 4999 then "A" else s"w${i + 1}"
      sb.append(s"w$i{$channel$op$threshold:A,$nextTarget}\n")
      i += 1
    // Add a dummy parts section (required by parser but not used by part2)
    sb.append("\n{x=1,m=1,a=1,s=1}\n")
    sb.toString

  // day21: Numeric keypad codes
  val day21Input: String =
    (0 until 20000).map(i => f"${(i * 37 + 13) % 1000}%03dA").mkString("\n")

  // day25: Graph with two clusters connected by 3 edges
  val day25Input: String =
    val sb = new StringBuilder
    val n = 75 // nodes per cluster
    // Cluster A: nodes a0..a(n-1), ring with 2-neighbor connections
    var i = 0
    while i < n do
      val neighbors = scala.collection.mutable.ArrayBuffer.empty[String]
      neighbors += s"a${(i + 1) % n}"
      neighbors += s"a${(i + 2) % n}"
      // Cross-edges
      if i == 0 then neighbors += "b0"
      if i == n / 3 then neighbors += s"b${n / 3}"
      if i == 2 * n / 3 then neighbors += s"b${2 * n / 3}"
      sb.append(s"a$i: ${neighbors.mkString(" ")}\n")
      i += 1
    // Cluster B: nodes b0..b(n-1), ring with 2-neighbor connections
    i = 0
    while i < n do
      sb.append(s"b$i: b${(i + 1) % n} b${(i + 2) % n}\n")
      i += 1
    sb.toString

// --- JMH benchmark suite ---

@Warmup(iterations = 10)
class RuntimeBenchmarksAocWeekly extends RuntimeBenchmarks:

  @Benchmark def day01Calibration: Unit =
    val result = aoc.day01.part2(AocBenchmarkData.day01Input)
    assert(result.toInt > 0)

  @Benchmark def day08Playground: Unit =
    val result = aoc.day08.part2(AocBenchmarkData.day08Input)
    assert(result != 0L)

  @Benchmark def day10PipeMaze: Unit =
    val result = aoc.day10.part2(AocBenchmarkData.day10Input)
    assert(result.toInt == 498 * 498) // (500-2)^2 enclosed tiles

  @Benchmark def day11CosmicExpansion: Unit =
    val result = aoc.day11.part2(AocBenchmarkData.day11Input)
    assert(result > 0L)

  @Benchmark def day12HotSprings: Unit =
    val result = aoc.day12.countAllUnfolded(AocBenchmarkData.day12Input)
    assert(result > 0L)

  @Benchmark def day13PointOfIncidence: Unit =
    val result = aoc.day13.part2(AocBenchmarkData.day13Input)
    assert(result >= 0)

  @Benchmark def day15LensLibrary: Unit =
    val result = aoc.day15.part2(AocBenchmarkData.day15Input)
    assert(result.toInt > 0)

  @Benchmark def day19Aplenty: Unit =
    val result = aoc.day19.part2(AocBenchmarkData.day19Input, 4001)
    assert(result > 0L)

  @Benchmark def day21KeypadConundrum: Unit =
    val result = aoc.day21.part2(AocBenchmarkData.day21Input)
    assert(result > 0L)

  @Benchmark def day25Snowverload: Unit =
    val result = aoc.day25.part1(AocBenchmarkData.day25Input)
    assert(result == 75 * 75)
