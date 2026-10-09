package bench.compilers

import dotty.tools.dotc.Driver
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.io.{AbstractFile, VirtualDirectory}

/** Compiler that directly calls Dotty's Driver API.
 *
 *  This is a simpler approach that bypasses the sbt bridge interface.
 *
 *  With `BENCH_OUTPUT=memory`, class and TASTy files are written to an in-memory `VirtualDirectory` instead of
 *  `outputDir`, so the measurement doesn't include file system writes.
 */
object DottyCompiler extends Compiler:
  val inMemory: Boolean = sys.env.get("BENCH_OUTPUT").contains("memory")

  def compile(sources: Seq[String], options: Seq[String], outputDir: String): Unit =
    val reporter =
      if inMemory then InMemoryOutputDriver.process((options ++ sources).toArray)
      else Driver().process(Array("-d", outputDir) ++ options ++ sources)
    if reporter.hasErrors then
      throw new CompilationFailedException(
        s"Compilation failed with errors: ${reporter.allErrors.mkString("\n")}",
      )

  /** A Driver whose output directory is a fresh in-memory directory for each compilation. */
  private object InMemoryOutputDriver extends Driver:
    override def setup(args: Array[String], rootCtx: Context): Option[(List[AbstractFile], Context)] =
      super.setup(args, rootCtx).map: (files, ctx) =>
        (files, ctx.fresh.setSetting(ctx.settings.outputDir, newVirtualDirectory()))

    // The constructor is `private[io]` from 3.10.0-RC1 (public in bytecode); before that it was public.
    private val ctor = classOf[VirtualDirectory].getConstructor(classOf[String], classOf[Option[?]])
    private def newVirtualDirectory(): AbstractFile =
      ctor.newInstance("<memory>", None).asInstanceOf[AbstractFile]
