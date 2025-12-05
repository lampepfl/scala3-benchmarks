package org.virtuslab.yaml.test

import org.junit.runner.JUnitCore
import org.junit.runner.notification.{Failure, RunListener}

object TestRunner:
  def main(args: Array[String]): Unit =
    val junit = new JUnitCore()

    var failures = 0
    var runs = 0

    junit.addListener(new RunListener {
      override def testFailure(failure: Failure): Unit =
        println(s"FAILED: ${failure.getDescription}")
        println(s"  ${failure.getMessage}")
        if failure.getTrace != null then
          failure.getTrace.split("\n").take(5).foreach(line => println(s"  $line"))

      override def testFinished(description: org.junit.runner.Description): Unit =
        runs += 1
    })

    val testClasses: Array[Class[?]] = Array(
      // Direct munit.FunSuite extensions
      classOf[org.virtuslab.yaml.ComposerSuite],
      classOf[org.virtuslab.yaml.ConstructSuite],
      classOf[org.virtuslab.yaml.NodeCreationSuite],
      classOf[org.virtuslab.yaml.YamlEncoderSuite],
      classOf[org.virtuslab.yaml.YamlEncoderJvmSuite],
      classOf[org.virtuslab.yaml.decoder.BaseDecoderErrorSuite],
      classOf[org.virtuslab.yaml.decoder.DecoderErrorsSuite],
      classOf[org.virtuslab.yaml.decoder.DecoderSuite],
      classOf[org.virtuslab.yaml.decoder.DockerYamlDecoderSuite],
      classOf[org.virtuslab.yaml.tokenizer.StringReaderSuite],
      classOf[org.virtuslab.yaml.traverse.NodeVisitorSuite],
      // BaseYamlSuite extensions
      classOf[org.virtuslab.yaml.YamlPackageSuite],
      classOf[org.virtuslab.yaml.parser.AnchorSpec],
      classOf[org.virtuslab.yaml.parser.CommentSpec],
      classOf[org.virtuslab.yaml.parser.DocumentStartEndSpec],
      classOf[org.virtuslab.yaml.parser.MappingSuite],
      classOf[org.virtuslab.yaml.parser.ParserSuite],
      classOf[org.virtuslab.yaml.parser.ScalarSpec],
      classOf[org.virtuslab.yaml.parser.SequenceSuite],
      classOf[org.virtuslab.yaml.parser.TagSuite],
      classOf[org.virtuslab.yaml.tokenizer.TagSuite],
      classOf[org.virtuslab.yaml.tokenizer.TokenizerSuite],
    )

    val result = junit.run(testClasses*)

    println()
    println(s"Tests: ${result.getRunCount}, Passed: ${result.getRunCount - result.getFailureCount}, Failed: ${result.getFailureCount}")

    if result.getFailureCount > 0 then
      result.getFailures.forEach { failure =>
        println(s"\nFailed: ${failure.getDescription}")
        println(s"Message: ${failure.getMessage}")
      }
      sys.exit(1)
