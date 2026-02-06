package clbg

/* The Computer Language Shootout, Copyright © 2004-2008 Brent Fulgham, 2005-2018 Isaac Gouy
   https://salsa.debian.org/benchmarksgame-team/archive-alioth-benchmarksgame/-/blob/master/LICENSE.md
   contributed by Isaac Gouy (Scala novice)
   updated for Scala 3 by Solal Pirelli, including adapting it to be a benchmark instead of reading stdin/out
   2nd version using more functional constructs
*/

object Ary2 {
  def main(): Int = {
    val n = 1000

    val x = (0 until n).map(i => i + 1).toArray
    val y = (0 until n).map(i => (0 until 1000).map(_ => x(i)).sum).toArray

    y(n-1)
  }
}


