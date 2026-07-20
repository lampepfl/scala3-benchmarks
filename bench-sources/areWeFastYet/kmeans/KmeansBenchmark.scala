package kmeans

import scala.collection.mutable
import scala.util.Random

class Point(val x: Double, val y: Double, val z: Double) {
  private def square(v: Double): Double = v * v
  def squareDistance(that: Point): Double = {
    square(that.x - x) + square(that.y - y) + square(that.z - z)
  }
  private def round(v: Double): Double = (v * 100).toInt / 100.0
  override def toString =
    "(" + round(x) + ", " + round(y) + ", " + round(z) + ")"
}

object KmeansBenchmark {

  def generatePoints(k: Int, num: Int): scala.collection.Seq[Point] = {
    val randx = new Random(1)
    val randy = new Random(3)
    val randz = new Random(5)
    val points = (0 until num)
      .map({ i =>
        val x = ((i + 1) % k) * 1.0 / k + randx.nextDouble() * 0.5
        val y = ((i + 5) % k) * 1.0 / k + randy.nextDouble() * 0.5
        val z = ((i + 7) % k) * 1.0 / k + randz.nextDouble() * 0.5
        new Point(x, y, z)
      })
    mutable.ArrayBuffer.from(points)
  }

  def initializeMeans(k: Int, points: scala.collection.Seq[Point]): scala.collection.Seq[Point] = {
    val rand = new Random(7)
    mutable.ArrayBuffer.from((0 until k).map(_ => points(rand.nextInt(points.length))))
  }

  // The loops in this file are written with index-based `while` on purpose: with
  // `foreach`/`for` the JIT sometimes fails to inline the closure through the
  // generic `IterableOnceOps.foreach`, leaving the benchmark 2-3x slower for the
  // whole JVM lifetime depending on compile-queue timing.
  def findClosest(p: Point, means: scala.collection.Seq[Point]): Point = {
    scala.Predef.assert(means.size > 0)
    var minDistance = p.squareDistance(means(0))
    var closest     = means(0)
    var i           = 0
    while (i < means.length) {
      val mean     = means(i)
      val distance = p.squareDistance(mean)
      if (distance < minDistance) {
        minDistance = distance
        closest = mean
      }
      i += 1
    }
    closest
  }

  def classify(
      points: scala.collection.Seq[Point],
      means: scala.collection.Seq[Point]
  ): scala.collection.Map[Point, scala.collection.Seq[Point]] = {
    val grouped = points.groupBy(p => findClosest(p, means))
    means.foldLeft(grouped) { (map, mean) =>
      if (map.contains(mean)) map else map.updated(mean, Seq())
    }
  }

  def findAverage(oldMean: Point, points: scala.collection.Seq[Point]): Point =
    if (points.length == 0) oldMean
    else {
      var x = 0.0
      var y = 0.0
      var z = 0.0
      var i = 0
      while (i < points.length) {
        val p = points(i)
        x += p.x
        y += p.y
        z += p.z
        i += 1
      }
      new Point(x / points.length, y / points.length, z / points.length)
    }

  def update(
      classified: scala.collection.Map[Point, scala.collection.Seq[Point]],
      oldMeans: scala.collection.Seq[Point]
  ): scala.collection.Seq[Point] = {
    oldMeans.map(mean => findAverage(mean, classified(mean)))
  }

  def converged(eta: Double)(
      oldMeans: scala.collection.Seq[Point],
      newMeans: scala.collection.Seq[Point]
  ): Boolean = {
    (oldMeans zip newMeans)
      .map({
        case (oldMean, newMean) =>
          oldMean.squareDistance(newMean)
      })
      .forall(_ <= eta)
  }

  final def kMeans(
      points: scala.collection.Seq[Point],
      means: scala.collection.Seq[Point],
      eta: Double
    ): scala.collection.Seq[Point] = {
    val classifiedPoints = classify(points, means)

    val newMeans = update(classifiedPoints, means)

    if (!converged(eta)(means, newMeans)) {
      kMeans(points, newMeans, eta)
    } else {
      newMeans
    }
  }

  def run(numPoints: Int): Boolean = {
    val eta = 0.01
    val k = 32
    val points = generatePoints(k, numPoints)
    val means = initializeMeans(k, points)
    val result = kMeans(points, means, eta)
    var sum = 0d
    var i   = 0
    while (i < result.length) {
      val p = result(i)
      sum += p.x
      sum += p.y
      sum += p.z
      i += 1
    }
    sum == 71.5437923802926D
  }
}
