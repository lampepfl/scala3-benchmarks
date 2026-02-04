package bench

import org.openjdk.jmh.annotations.{Benchmark, Warmup}
import org.virtuslab.yaml.YamlCodec

// --- scalaYaml data model ---

case class YBAddress(street: String, city: String, state: String, zip: String) derives YamlCodec
case class YBSkill(name: String, level: Int) derives YamlCodec

case class YBEmployee(
    id: Int,
    name: String,
    email: String,
    active: Boolean,
    salary: Double,
    address: YBAddress,
    skills: List[YBSkill],
    tags: List[String],
) derives YamlCodec

case class YBDepartment(name: String, budget: Double, employees: List[YBEmployee]) derives YamlCodec
case class YBOrganization(name: String, departments: List[YBDepartment]) derives YamlCodec

// --- scalaParserCombinators grammar (from Getting Started guide) ---

case class WordFreq(word: String, count: Int)

object WordFreqParser extends scala.util.parsing.combinator.RegexParsers:
  def word: Parser[String] = """[a-z]+""".r
  def number: Parser[Int] = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def freq: Parser[WordFreq] = word ~ number ^^ { case w ~ n => WordFreq(w, n) }
  def freqList: Parser[List[WordFreq]] = rep(freq)

  def apply(input: String): List[WordFreq] =
    parseAll(freqList, input) match
      case Success(result, _) => result
      case failure: NoSuccess => throw RuntimeException(s"Parse failed: ${failure.msg}")

// --- Benchmark input data ---

object LibsBenchmarkData:
  import org.virtuslab.yaml.*

  def generateOrganization(numDepartments: Int, employeesPerDept: Int): YBOrganization =
    YBOrganization(
      name = "Benchmark Corp",
      departments = (0 until numDepartments).map { d =>
        YBDepartment(
          name = s"Department-$d",
          budget = 100000.0 + d * 1000,
          employees = (0 until employeesPerDept).map { e =>
            val idx = d * employeesPerDept + e
            YBEmployee(
              id = idx,
              name = s"Employee-$idx",
              email = s"emp$idx@example.com",
              active = idx % 3 != 0,
              salary = 50000.0 + idx * 100,
              address = YBAddress(
                street = s"$idx Main St",
                city = s"City-${idx % 50}",
                state = s"S${idx % 50}",
                zip = f"${10000 + idx % 90000}%05d",
              ),
              skills = (0 until 3).map(s =>
                YBSkill(s"skill-${(idx * 3 + s) % 20}", (idx + s) % 10 + 1),
              ).toList,
              tags = (0 until 4).map(t => s"tag-${(idx + t) % 30}").toList,
            )
          }.toList,
        )
      }.toList,
    )

  // 10 departments x 100 employees = 1000 employees with nested data
  val organization: YBOrganization = generateOrganization(10, 100)
  val organizationYaml: String = organization.asYaml

  // ~50000 "word number" lines for the parser combinator benchmark
  def generateWordFreqInput(n: Int): String =
    val words = Array(
      "alpha",
      "bravo",
      "charlie",
      "delta",
      "echo",
      "foxtrot",
      "golf",
      "hotel",
      "india",
      "juliet",
    )
    val sb = new StringBuilder
    var i = 0
    while i < n do
      sb.append(words(i % words.length))
      sb.append(' ')
      sb.append((i * 37 + 13) % 10000)
      sb.append('\n')
      i += 1
    sb.toString

  val wordFreqInput: String = generateWordFreqInput(50000)

  // ~500K chars of plain text for the fansi benchmark
  def generatePlainText(numLines: Int, charsPerLine: Int): String =
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .,;:!?"
    val sb = new StringBuilder
    var i = 0
    while i < numLines do
      var j = 0
      while j < charsPerLine do
        sb.append(alphabet((i * charsPerLine + j) % alphabet.length))
        j += 1
      sb.append('\n')
      i += 1
    sb.toString

  val fansiPlainText: String = generatePlainText(10000, 100)

  val fansiColors: Array[fansi.Attrs] = Array(
    fansi.Color.Red,
    fansi.Color.Green,
    fansi.Color.Blue,
    fansi.Color.Yellow,
    fansi.Color.Cyan,
    fansi.Color.Magenta,
    fansi.Bold.On,
    fansi.Underlined.On,
    fansi.Reversed.On,
    fansi.Color.LightRed,
    fansi.Color.LightGreen,
    fansi.Color.LightBlue,
  )

// --- JMH benchmark suite ---

@Warmup(iterations = 10)
class RuntimeBenchmarksLibsWeekly extends RuntimeBenchmarks:
  import org.virtuslab.yaml.*

  @Benchmark def yamlEncode: Unit =
    val yaml = LibsBenchmarkData.organization.asYaml
    assert(yaml.length > 50000)

  @Benchmark def yamlDecode: Unit =
    val result = LibsBenchmarkData.organizationYaml.as[YBOrganization]
    val org = result.toOption.get
    assert(org.departments.size == 10)
    assert(org.departments.head.employees.size == 100)

  @Benchmark def parserCombinatorsWordFreq: Unit =
    val result = WordFreqParser(LibsBenchmarkData.wordFreqInput)
    assert(result.size == 50000)

  @Benchmark def fansiFormat: Unit =
    val text = LibsBenchmarkData.fansiPlainText
    val str = fansi.Str(text)
    val colors = LibsBenchmarkData.fansiColors
    val chunkSize = 50
    val overlays = new scala.collection.mutable.ArrayBuffer[(fansi.Attrs, Int, Int)]
    var i = 0
    while i + chunkSize <= str.length do
      overlays += ((colors(i / chunkSize % colors.length), i, i + chunkSize))
      i += chunkSize
    val styled = str.overlayAll(overlays.toSeq)
    val rendered = styled.render
    assert(rendered.length > text.length)
