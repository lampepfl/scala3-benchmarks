//> using scala 3.8.1
//> using jvm temurin:25
//> using dep "com.lihaoyi::os-lib:0.11.8"
//> using dep "com.lihaoyi::upickle:4.4.2"
//> using dep "com.github.tototoshi::scala-csv:2.0.0"

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import upickle.default.{read, ReadWriter}

case class JmhMetric(rawData: Seq[Seq[Double]]) derives ReadWriter

case class JmhBenchmark(
    benchmark: String,
    warmupIterations: Int,
    measurementIterations: Int,
    primaryMetric: JmhMetric,
    secondaryMetrics: Map[String, JmhMetric],
) derives ReadWriter

case class Stats(count: Int, min: Double, avg: Double, max: Double):
  def map(f: Double => Double): Stats =
    Stats(count, f(min), f(avg), f(max))

  def merge(other: Stats): Stats =
    val totalCount = this.count + other.count
    val combinedAvg = (this.avg * this.count + other.avg * other.count) / totalCount
    Stats(
      totalCount,
      Math.min(this.min, other.min),
      combinedAvg,
      Math.max(this.max, other.max),
    )

  def toMap: Map[String, String] =
    Map(
      "count" -> count.toString,
      "min" -> formatSigFigs(min),
      "avg" -> formatSigFigs(avg),
      "max" -> formatSigFigs(max),
    )

def metricStats(metric: JmhMetric): Stats =
  val values = metric.rawData.flatten
  assert(values.nonEmpty, "Metric rawData should not be empty")
  Stats(values.size, values.min, values.sum / values.size, values.max)

def appendStats(file: os.Path, version: String, stats: Stats): Unit =
  // Read existing CSV rows if file exists, otherwise start with empty
  val csvRows =
    if os.exists(file) then
      val reader = CSVReader.open(file.toIO)
      val rows = reader.allWithHeaders()
      reader.close()
      rows
    else
      Seq.empty

  // Partition existing rows into those matching the version and others
  val (others, existing) = csvRows.partition(_("version") != version)

  // Combine previous stats with new stats if version already exists, otherwise use new stats
  val updatedRow: Map[String, String] =
    if existing.isEmpty then
      stats.toMap + ("version" -> version)
    else
      assert(existing.size == 1, s"Multiple entries found for version $version in $file")
      val previousRow = existing.head
      val combinedStats = Stats(
        previousRow("count").toInt,
        previousRow("min").toDouble,
        previousRow("avg").toDouble,
        previousRow("max").toDouble,
      ).merge(stats)
      combinedStats.toMap + ("version" -> version)

  // Write back all rows (others + updatedRow) to CSV
  os.makeDir.all(file / os.up) // Ensure parent directory exists
  val writer = CSVWriter.open(file.toIO)
  val header = Seq("version", "count", "min", "avg", "max")
  writer.writeRow(header)
  for row <- others do
    writer.writeRow(header.map(h => row(h)))
  writer.writeRow(header.map(h => updatedRow(h)))
  writer.close()

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

  // Extract from results/<machine>/<jvm>/<version>/<timestamp>.json
  val segments = jsonPath.segments.toSeq
  val machine = segments(segments.length - 4)
  val jvm = segments(segments.length - 3)
  val version = segments(segments.length - 2)
  val patchVersion = version.take(5)
  val benchmarks = read[Seq[JmhBenchmark]](os.read(jsonPath))
  assert(benchmarks.nonEmpty, s"No benchmarks found in JSON file: $jsonPath")

  // Generate run datetime (ISO format without separators)
  val formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
  val runDatetime = formatter.format(Instant.now().atZone(ZoneId.of("UTC")))

  // Create output directory: raw/<machine>/<jvm>/<branch>/<version>/
  val outputDir = dataRepoPath / "raw" / machine / jvm / branch / patchVersion / version
  os.makeDir.all(outputDir)

  val outputPath = outputDir / s"$runDatetime.csv"
  val writer = CSVWriter.open(outputPath.toIO)
  val header = Seq(
    "benchmark",
    "warmup_iterations",
    "iterations",
    "times",
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
    val shortBenchmark = bench.benchmark.split('.').last
    val times = bench.primaryMetric.rawData.flatten
    val allocsStats = metricStats(bench.secondaryMetrics("gc.alloc.rate.norm")).map(_ / 1e6) // Convert to MB
    val gcStats = metricStats(bench.secondaryMetrics("gc.count"))
    val compStats = metricStats(bench.secondaryMetrics("compiler.time.profiled"))
    writer.writeRow(Seq(
      shortBenchmark,
      bench.warmupIterations.toString,
      bench.measurementIterations.toString,
      times.map(formatSigFigs(_)).mkString(" "),
    ) ++ Seq(
      allocsStats.min,
      allocsStats.avg,
      allocsStats.max,
      gcStats.min,
      gcStats.avg,
      gcStats.max,
      compStats.min,
      compStats.avg,
      compStats.max,
    ).map(formatSigFigs(_)))

    val aggregatePath = dataRepoPath / "aggregated" / machine / jvm / branch / patchVersion
    val timeStats = metricStats(bench.primaryMetric)
    appendStats(aggregatePath / "time" / s"$shortBenchmark.csv", version, timeStats)
    appendStats(aggregatePath / "allocs" / s"$shortBenchmark.csv", version, allocsStats)
    appendStats(aggregatePath / "gc" / s"$shortBenchmark.csv", version, gcStats)
    appendStats(aggregatePath / "comp" / s"$shortBenchmark.csv", version, compStats)

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
