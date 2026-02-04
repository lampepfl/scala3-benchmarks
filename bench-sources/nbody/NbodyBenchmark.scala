/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * Based on nbody.java and adapted basde on the SOM version.
 */
package nbody

object NbodyBenchmark {
  def run(n: Int): Boolean = {
    val system = new NBodySystem()

    var i = 0
    while (i < n) {
      system.advance(0.01)
      i += 1
    }

    system.energy() == -0.1690859889909308
  }
}
