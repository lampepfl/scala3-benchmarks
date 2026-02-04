// Adapted from https://github.com/scalacenter/scala-advent-of-code
// 2025 Day 08: Playground
package aoc.day08

import scala.util.boundary
import scala.util.boundary.break

/** A junction box in 3D space with an associated circuit ID. */
case class Box(val x: Long, val y: Long, val z: Long, var circuit: Int):
  def distanceSquare(other: Box): Long =
    (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z)

/** Parses comma-separated coordinates from the given `line` into a `Box` with
  * the given `circuit` ID.
  */
def parseBox(line: String, circuit: Int): Box =
  val parts = line.split(",")
  Box(parts(0).toLong, parts(1).toLong, parts(2).toLong, circuit)

/** Parses the input, returning a sequence of `Box`es and all unique pairs
  * of boxes sorted by distance.
  */
def load(input: String): (Seq[Box], Seq[(Box, Box)]) =
  val lines = input.linesIterator.filter(_.nonEmpty)
  val boxes = lines.zipWithIndex.map(parseBox).toSeq
  val pairsByDistance = boxes.pairs.toSeq.sortBy((b1, b2) => b1.distanceSquare(b2))
  (boxes, pairsByDistance)

extension [T](self: Seq[T])
  /** Generates all unique pairs (combinations of 2) from the sequence. */
  def pairs: Iterator[(T, T)] =
    self.combinations(2).map(pair => (pair(0), pair(1)))

/** Sets all boxes with circuit `c2` to circuit `c1`. */
def merge(c1: Int, c2: Int, boxes: Seq[Box]): Unit =
  for b <- boxes if b.circuit == c2 do b.circuit = c1

def part2(input: String): Long =
  val (boxes, pairsByDistance) = load(input)
  var n = boxes.length
  boundary:
    for (b1, b2) <- pairsByDistance if b1.circuit != b2.circuit do
      merge(b1.circuit, b2.circuit, boxes)
      n -= 1
      if n <= 1 then
        break(b1.x * b2.x)
    throw Exception("Should not reach here")
