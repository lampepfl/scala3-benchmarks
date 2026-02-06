package clbg

/* The Computer Language Shootout, Copyright © 2004-2008 Brent Fulgham, 2005-2018 Isaac Gouy
   https://salsa.debian.org/benchmarksgame-team/archive-alioth-benchmarksgame/-/blob/master/LICENSE.md
   contributed by Isaac Gouy (Scala novice)
   updated for Scala 3 by Solal Pirelli, including adapting it to be a benchmark instead of reading stdin/out
   2nd version using more functional constructs
*/

object NestedLoop2 {
  def main(): Int =
    val n = 10
    val count = (0 until n)
        .flatMap(_ => 0 until n)
        .flatMap(_ => 0 until n)
        .flatMap(_ => 0 until n)
        .flatMap(_ => 0 until n)
        .flatMap(_ => 0 until n)
        .size
    count
}