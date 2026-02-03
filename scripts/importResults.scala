//> using scala 3.8.1
//> using jvm temurin:25
//> using dep "com.lihaoyi::os-lib:0.11.8"
//> using dep "com.lihaoyi::upickle:4.4.2"
//> using dep "com.github.tototoshi::scala-csv:2.0.0"

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter

import com.github.tototoshi.csv.CSVWriter
import upickle.default.{read, ReadWriter}

// Parse JMH JSON result
case class JmhMetric(
    score: Double,
    scoreError: Double,
    scorePercentiles: Map[String, Double],
    rawData: Seq[Seq[Double]],
) derives ReadWriter

case class JmhBenchmark(
    benchmark: String,
    warmupIterations: Int,
    measurementIterations: Int,
    primaryMetric: JmhMetric,
    secondaryMetrics: Map[String, JmhMetric],
) derives ReadWriter

def metricStats(metric: Option[JmhMetric]): (Double, Double, Double) =
  metric match
    case Some(m) =>
      val values = m.rawData.flatten
      assert(values.nonEmpty, "Metric rawData should not be empty")
      (values.min, values.sum / values.size, values.max)
    case None =>
      throw Exception(s"Metric not found")

def formatSigFigs(d: Double, sigFigs: Int = 4): String =
  if d == 0.0 then "0"
  else
    // Calculate decimal places needed for desired significant figures
    // Note: We use 'f' format instead of 'g' to avoid scientific notation
    val digits = sigFigs - 1 - Math.floor(Math.log10(Math.abs(d))).toInt
    s"%.${Math.max(digits, 0)}f".format(d)
      .replaceAll("0+$", "") // Strip trailing zeros
      .replaceAll("\\.$", "") // Strip trailing decimal point

def importResults(
    jsonPath: os.Path,
    dataRepoPath: os.Path,
    branch: String,
): Unit =
  println(s"Reading JMH results from: $jsonPath")

  // Extract machine, jvm, version from path: results/<machine>/<jvm>/<version>/<timestamp>.json
  val pathSegments = jsonPath.segments.toSeq
  val resultsIndex = pathSegments.indexOf("results")
  assert(resultsIndex >= 0, s"Path must contain 'results' directory: $jsonPath")
  assert(pathSegments.size >= resultsIndex + 4, s"Invalid path structure: $jsonPath (expected results/<machine>/<jvm>/<version>/<file>.json)")

  val machine = pathSegments(resultsIndex + 1)
  val jvm = pathSegments(resultsIndex + 2)
  val scalaVersion = pathSegments(resultsIndex + 3)

  println(s"Extracted from path: machine=$machine, jvm=$jvm, version=$scalaVersion")

  val benchmarks = read[Seq[JmhBenchmark]](os.read(jsonPath))

  if benchmarks.isEmpty then
    println("Warning: No benchmarks found in JSON file")
    return

  println(s"Found ${benchmarks.size} benchmark(s)")

  // Generate run datetime (ISO format without separators)
  val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
  val runDatetime = formatter.format(Instant.now().atZone(ZoneId.of("UTC")))

  // Create output directory: raw/<machine>/<jvm>/<branch>/<version>/
  val outputDir = dataRepoPath / "raw" / machine / jvm / branch / scalaVersion
  os.makeDir.all(outputDir)

  // Write CSV file
  val outputPath = outputDir / s"$runDatetime.csv"
  val writer = CSVWriter.open(outputPath.toIO)

  val header = Seq(
    "benchmark",
    "warmup_iterations",
    "iterations",
    "t0",
    "t1",
    "t2",
    "t3",
    "t4",
    "t5",
    "t6",
    "t7",
    "t8",
    "t9",
    "allocs_min",
    "allocs_avg",
    "allocs_max",
    "gc_min",
    "gc_avg",
    "gc_max",
    "comp_min",
    "comp_avg",
    "comp_max",
  )

  writer.writeRow(header)

  for bench <- benchmarks do
    val times = bench.primaryMetric.rawData.flatten.padTo(10, 0.0).take(10)
    val (allocsMin, allocsAvg, allocsMax) = metricStats(bench.secondaryMetrics.get("gc.alloc.rate.norm"))
    val allocsMB = (allocsMin / 1e6, allocsAvg / 1e6, allocsMax / 1e6)
    val (gcMin, gcAvg, gcMax) = metricStats(bench.secondaryMetrics.get("gc.count"))
    val (compMin, compAvg, compMax) = metricStats(bench.secondaryMetrics.get("compiler.time.profiled"))

    writer.writeRow(Seq(
      bench.benchmark,
      bench.warmupIterations.toString,
      bench.measurementIterations.toString,
    ) ++ times.map(formatSigFigs(_)) ++ Seq(
      allocsMB._1,
      allocsMB._2,
      allocsMB._3,
      gcMin,
      gcAvg,
      gcMax,
      compMin,
      compAvg,
      compMax,
    ).map(formatSigFigs(_)))
  
  writer.close()

  println(s"Wrote results to: $outputPath")

@main def run(
    jsonPathStr: String,
    dataRepoPathStr: String,
    branch: String,
): Unit =
  val jsonPath = os.Path(jsonPathStr, os.pwd)
  val dataRepoPath = os.Path(dataRepoPathStr, os.pwd)
  assert(os.exists(jsonPath), s"JSON file not found: $jsonPath")
  assert(os.exists(dataRepoPath), s"Data repository not found: $dataRepoPath")
  importResults(jsonPath, dataRepoPath, branch)
