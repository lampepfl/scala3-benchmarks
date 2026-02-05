// Adapted from https://github.com/scalacenter/scala-advent-of-code
// 2023 Day 01: Trebuchet?!
package aoc.day01

/** The textual representation of digits. */
val stringDigitReprs = Map(
  "one" -> 1,
  "two" -> 2,
  "three" -> 3,
  "four" -> 4,
  "five" -> 5,
  "six" -> 6,
  "seven" -> 7,
  "eight" -> 8,
  "nine" -> 9,
)

/** All the string representation of digits, including the digits themselves. */
val digitReprs = stringDigitReprs ++ (1 to 9).map(i => i.toString() -> i)

def part2(input: String): String =
  // A regex that matches any of the keys of `digitReprs`
  val digitReprRegex = digitReprs.keysIterator.mkString("|").r

  def lineToCoordinates(line: String): Int =
    val matchesIter =
      for
        lineTail <- line.tails
        oneMatch <- digitReprRegex.findPrefixOf(lineTail)
      yield
        oneMatch
    val matches = matchesIter.toList

    // Convert the string representations into actual digits and form the result
    val firstDigit = digitReprs(matches.head)
    val lastDigit = digitReprs(matches.last)
    s"$firstDigit$lastDigit".toInt
  end lineToCoordinates

  // Process lines as in part1
  val result = input
    .linesIterator
    .map(lineToCoordinates(_))
    .sum
  result.toString()
end part2
