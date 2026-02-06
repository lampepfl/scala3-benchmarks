package clbg

/* The Computer Language Shootout, Copyright © 2004-2008 Brent Fulgham, 2005-2018 Isaac Gouy
   https://salsa.debian.org/benchmarksgame-team/archive-alioth-benchmarksgame/-/blob/master/LICENSE.md
   contributed by Isaac Gouy (Scala novice)
   updated for Scala 3 by Solal Pirelli, including adapting it to be a benchmark instead of reading stdin/out
*/

object Ary {
  def main(): Int = {
    val n = 1000

    val x = new Array[Int](n)
    for i <- Iterator.range(0, n) do x(i) = i + 1

    val y = new Array[Int](n)
    for
      j <- Iterator.range(0, 1000)
      i <- Iterator.range(0, n)
    do
      y(i) = y(i) + x(i)

    y(n-1)
  }
}


