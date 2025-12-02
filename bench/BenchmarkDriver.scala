package bench

import dotty.tools.dotc.Driver
import dotty.tools.dotc.core.Contexts.{Context, ContextBase}
import dotty.tools.dotc.config.JavaPlatform
import dotty.tools.io.{AbstractFile, ClassPath, VirtualDirectory}

/** A custom Driver that uses a preloaded classpath and virtual output directory. */
class BenchmarkDriver(preloadedClasspath: ClassPath, outputDir: VirtualDirectory) extends Driver:
  override protected def initCtx: Context =
    val base = new ContextBase:
      override protected def newPlatform(using Context): JavaPlatform =
        new BenchmarkPlatform(preloadedClasspath)
    val ctx = base.initialCtx.fresh
    ctx.setSetting(ctx.settings.outputDir, outputDir)
    ctx

/** A custom JavaPlatform that uses the preloaded classpath. */
class BenchmarkPlatform(preloadedClasspath: ClassPath) extends JavaPlatform:
  override def classPath(using Context): ClassPath = preloadedClasspath
