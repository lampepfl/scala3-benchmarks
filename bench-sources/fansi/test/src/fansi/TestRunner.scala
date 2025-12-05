package test.fansi

import utest._

object Main:
  def main(args: Array[String]): Unit =
    val results = TestRunner.runAndPrint(
      FansiTests.tests,
      "FansiTests"
    )
    val (summary, successes, failures) = TestRunner.renderResults(
      Seq("FansiTests" -> results)
    )
    println(summary)
    if failures > 0 then sys.exit(1)
