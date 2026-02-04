package kmeans

import scala.collection._
import scala.util.Random

class Point(val x: Double, val y: Double, val z: Double) {
  private def square(v: Double): Double = v * v
  def squareDistance(that: Point): Double = {
    square(that.x - x) + square(that.y - y) + square(that.z - z)
  }
  private def round(v: Double): Double = (v * 100).toInt / 100.0
  override def toString =
    "(" + round(x) + "}, " + round(y) + ", " + round(z) + ")"
}

object KmeansBenchmark extends communitybench.Benchmark {
  def inputOutput: (String, String) = ("100000", "true")

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
    mutable.ArrayBuffer(points: _*)
  }

  def initializeMeans(k: Int, points: Seq[Point]): Seq[Point] = {
    val rand = new Random(7)
    mutable.ArrayBuffer((0 until k).map(_ => points(rand.nextInt(points.length))): _*)
  }

  def findClosest(p: Point, means: scala.collection.Seq[Point]): Point = {
    scala.Predef.assert(means.size > 0)
    var minDistance = p.squareDistance(means(0))
    var closest     = means(0)
    for (mean <- means) {
      val distance = p.squareDistance(mean)
      if (distance < minDistance) {
        minDistance = distance
        closest = mean
      }
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
      points.foreach { p =>
        x += p.x
        y += p.y
        z += p.z
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
          oldMean squareDistance newMean
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

  def run(input: String): Boolean = {
    val numPoints = input.toInt
    val eta = 0.01
    val k = 32
    val points = generatePoints(k, numPoints)
    val means = initializeMeans(k, points)
    var centers: scala.collection.Seq[Point] = null
    val result = kMeans(points, means, eta)
    var sum = 0d
    result.foreach { p =>
      sum += p.x
      sum += p.y
      sum += p.z
    }
    sum == 71.5437923802926D
  }
}
