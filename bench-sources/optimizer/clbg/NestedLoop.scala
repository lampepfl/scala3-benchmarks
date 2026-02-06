package clbg

/* The Computer Language Shootout, Copyright © 2004-2008 Brent Fulgham, 2005-2018 Isaac Gouy
   https://salsa.debian.org/benchmarksgame-team/archive-alioth-benchmarksgame/-/blob/master/LICENSE.md
   contributed by Isaac Gouy (Scala novice)
   updated for Scala 3 by Solal Pirelli, including adapting it to be a benchmark instead of reading stdin/out
*/

object NestedLoop {
  def main(): Int = {
    val n = 10
    var count = 0

    for
      a <- Iterator.range(0, n)
      b <- Iterator.range(0, n)
      c <- Iterator.range(0, n)
      d <- Iterator.range(0, n)
      e <- Iterator.range(0, n)
      f <- Iterator.range(0, n)
    do
      count = count + 1

    count
  }
}