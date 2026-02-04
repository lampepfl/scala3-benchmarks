// Adapted from https://github.com/scalacenter/scala-advent-of-code
// 2023 Day 19: Aplenty
package aoc.day19

/*-----------------*/
/* Data structures */
/*-----------------*/

enum Channel:
  case X, M, A, S

enum Operator:
  case LessThan, GreaterThan

enum Result:
  case Reject, Accept

enum Instruction:
  case IfThenElse(
      channel: Channel,
      operator: Operator,
      value: Int,
      thenBranch: GoTo | Return,
      elseBranch: Instruction
  )
  case Return(result: Result)
  case GoTo(target: String)

import Instruction.*

type Workflow = Map[String, Instruction]

/*---------*/
/* Parsing */
/*---------*/

object Channel:
  def parse(input: String): Channel =
    input match
      case "x" => Channel.X
      case "m" => Channel.M
      case "a" => Channel.A
      case "s" => Channel.S
      case _   => throw Exception(s"Invalid channel: $input")

object Operator:
  def parse(input: String): Operator =
    input match
      case "<" => Operator.LessThan
      case ">" => Operator.GreaterThan
      case _   => throw Exception(s"Invalid operator: $input")

object Result:
  def parse(input: String): Result =
    input match
      case "R" => Result.Reject
      case "A" => Result.Accept
      case _   => throw Exception(s"Invalid result: $input")

object Instruction:
  private val IfThenElseRegex = """([xmas])([<>])(\d+):(\w+),(.*)""".r
  private val ReturnRegex = """([RA])""".r
  private val GoToRegex = """(\w+)""".r
  def parse(input: String): Instruction =
    input match
      case IfThenElseRegex(channel, operator, value, thenBranch, elseBranch) =>
        Instruction.parse(thenBranch) match
          case thenBranch: (GoTo | Return) =>
            IfThenElse(
              Channel.parse(channel),
              Operator.parse(operator),
              value.toInt,
              thenBranch,
              Instruction.parse(elseBranch)
            )
          case _ => throw Exception(s"Invalid then branch: $thenBranch")
      case ReturnRegex(result) => Return(Result.parse(result))
      case GoToRegex(target)   => GoTo(target)
      case _ => throw Exception(s"Invalid instruction: $input")

object Workflow:
  def parse(input: String): Workflow =
    input.split("\n").map(parseBlock).toMap

  private val BlockRegex = """(\w+)\{(.*?)\}""".r
  private def parseBlock(input: String): (String, Instruction) =
    input match
      case BlockRegex(label, body) =>
        (label, Instruction.parse(body))

/*------------------------------*/
/* Abstract evaluation (part 2) */
/*------------------------------*/

case class Range(from: Long, until: Long):
  assert(from < until)
  def count() = until - from

object Range:
  def safe(from: Long, until: Long): Option[Range] =
    if from < until then Some(Range(from, until)) else None

case class AbstractPart(x: Range, m: Range, a: Range, s: Range):
  def count() = x.count() * m.count() * a.count() * s.count()

  def withChannel(channel: Channel, newRange: Range) =
    channel match
      case Channel.X => copy(x = newRange)
      case Channel.M => copy(m = newRange)
      case Channel.A => copy(a = newRange)
      case Channel.S => copy(s = newRange)

  def getChannel(channel: Channel) =
    channel match
      case Channel.X => x
      case Channel.M => m
      case Channel.A => a
      case Channel.S => s

  def split(
      channel: Channel,
      value: Int
  ): (Option[AbstractPart], Option[AbstractPart]) =
    val currentRange = getChannel(channel)
    (
      Range.safe(currentRange.from, value).map(withChannel(channel, _)),
      Range.safe(value, currentRange.until).map(withChannel(channel, _))
    )

def part2(input: String, until: Long): Long =
  val Array(workflowLines, _) = input.split("\n\n")
  val workflow = Workflow.parse(workflowLines.trim())

  def count(part: AbstractPart, instruction: Instruction): Long =
    instruction match
      case IfThenElse(channel, operator, value, thenBranch, elseBranch) =>
        val (trueValues, falseValues) =
          operator match
            case Operator.LessThan    => part.split(channel, value)
            case Operator.GreaterThan => part.split(channel, value + 1).swap
        trueValues.map(count(_, thenBranch)).getOrElse(0L)
          + falseValues.map(count(_, elseBranch)).getOrElse(0L)
      case Return(Result.Accept) => part.count()
      case Return(Result.Reject) => 0L
      case GoTo(target)          => count(part, workflow(target))

  count(
    AbstractPart(
      Range(1, until),
      Range(1, until),
      Range(1, until),
      Range(1, until)
    ),
    workflow("in")
  )
