// Adapted from https://github.com/scalacenter/scala-advent-of-code
// 2023 Day 13: Point of Incidence
package aoc.day13

import scala.collection.mutable.Buffer

type Tile = '.' | '#'
type Line = Seq[Tile]
type Pattern = Seq[Line]

def part2(input: Seq[String]) =
  parseInput(input)
    .flatMap: pattern =>
      findReflectionWithSmudge(pattern).map(100 * _)
        .orElse(findReflectionWithSmudge(pattern.transpose))
    .sum

def parseInput(input: Seq[String]): Seq[Pattern] =
  val currentPattern = Buffer.empty[Line]
  val patterns = Buffer.empty[Pattern]
  def addPattern() =
    patterns += currentPattern.toSeq
    currentPattern.clear()
  for lineStr <- input do
    if lineStr.isEmpty then addPattern()
    else
      val line = lineStr.collect[Tile] { case tile: Tile => tile }
      currentPattern += line
  addPattern()
  patterns.toSeq

def findReflectionWithSmudge(pattern: Pattern): Option[Int] =
  1.until(pattern.size).find: i =>
    val (leftPart, rightPart) = pattern.splitAt(i)
    val smudges = leftPart.reverse
      .zip(rightPart)
      .map((l1, l2) => l1.zip(l2).count(_ != _))
      .sum
    smudges == 1
