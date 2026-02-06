package kostya

// Original code Copyright (c) 2014 Konstantin Makarchev, MIT licensed
// https://github.com/kostya/benchmarks/blob/master/LICENSE
// Adapted by Solal Pirelli to remove the kostya/benchmarks specific stuff, use Scala 3 idioms, and be a benchmark instead of reading stdin/out

object MatMul {
  type Matrix = Array[Array[Double]]

  def matgen(n: Int, seed: Double): Matrix =
    val a = Array.ofDim[Double](n, n)
    val tmp = seed / n / n
    for
      i <- 0 until n
      j <- 0 until n
    do a(i)(j) = tmp * (i - j) * (i + j)
    a

  def matmul(a: Matrix, b: Matrix): Matrix = {
    val m = a.length
    val n = a(0).length
    val p = b(0).length

    // transpose
    val b2 = Array.ofDim[Double](n, p)
    for
      i <- 0 until n
      j <- 0 until p
    do b2(j)(i) = b(i)(j)

    // multiplication
    val c = Array.ofDim[Double](m, p)
    for
      i <- 0 until m
      j <- 0 until p
    do
      var s = 0.0
      val ai = a(i)
      val b2j = b2(j)
      for k <- 0 until n do s += ai(k) * b2j(k)
      c(i)(j) = s

    c
  }

  def calc(n: Int): Double =
    val size = n / 2 * 2
    val a = matgen(size, 1.0)
    val b = matgen(size, 2.0)
    val x = matmul(a, b)
    x(size / 2)(size / 2)

  def main(): Double = {
    val n = 100

    val left = calc(101)
    val right = -18.67
    if Math.abs(left - right) > 0.1 then
      System.err.println(s"$left != $right")
      System.exit(1)

    calc(n)
  }
}