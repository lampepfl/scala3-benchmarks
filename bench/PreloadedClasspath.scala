package bench

import java.io.File
import java.nio.file.{Files, Path}
import java.util.zip.ZipFile
import scala.jdk.CollectionConverters.*

import dotty.tools.io.{AbstractFile, VirtualDirectory, VirtualFile}
import dotty.tools.dotc.classpath.{AggregateClassPath, VirtualDirectoryClassPath, DirectoryClassPath, JrtClassPath}
import dotty.tools.io.ClassPath

/** Preloads classpath entries (jars) into memory as VirtualDirectories.
  *
  * This avoids repeated I/O and unzipping during compilation, which can
  * significantly reduce benchmark noise from filesystem operations.
  */
object PreloadedClasspath:
  /** Preload all jars from a classpath string.
    * Returns a ClassPath that can be used with the compiler.
    * Includes the JRT (Java Runtime) classpath for java.* classes.
    */
  def preloadClasspath(classpath: String): ClassPath =
    val entries = classpath.split(File.pathSeparator).toSeq.filter(_.nonEmpty)
    val classPathEntries = entries.map { entry =>
      val path = Path.of(entry)
      if entry.endsWith(".jar") && Files.isRegularFile(path) then
        VirtualDirectoryClassPath(loadJarToVirtualDirectory(entry))
      else if Files.isDirectory(path) then
        new DirectoryClassPath(path.toFile)
      else
        throw new IllegalArgumentException(s"Unsupported classpath entry: $entry")
    }
    // Include JRT classpath for java.lang.Object etc.
    val jrtClasspath = JrtClassPath(release = None).toSeq
    AggregateClassPath((jrtClasspath ++ classPathEntries).toIndexedSeq)

  /** Load a jar file completely into memory as a VirtualDirectory. */
  private def loadJarToVirtualDirectory(jarPath: String): VirtualDirectory =
    val root = new VirtualDirectory(jarPath, None)
    val zipFile = new ZipFile(jarPath)
    try
      val entries = zipFile.entries().asScala
      for entry <- entries if !entry.isDirectory do
        val path = entry.getName
        val content = zipFile.getInputStream(entry).readAllBytes()

        // Create directory structure
        val parts = path.split("/")
        var currentDir = root
        for part <- parts.init do
          currentDir.lookupName(part, directory = true) match
            case null =>
              currentDir = currentDir.subdirectoryNamed(part).asInstanceOf[VirtualDirectory]
            case dir: VirtualDirectory =>
              currentDir = dir
            case other =>
              throw new IllegalStateException(s"Expected directory but found: $other")

        // Create the file
        val fileName = parts.last
        val file = currentDir.fileNamed(fileName)
        val out = file.output
        try out.write(content)
        finally out.close()
    finally
      zipFile.close()
    root

