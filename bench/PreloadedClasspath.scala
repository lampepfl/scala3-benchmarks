package bench

import java.io.File
import java.net.URI
import java.nio.file.{Files, FileSystem, FileSystems, Path}
import java.util.zip.ZipFile

import scala.jdk.CollectionConverters.*

import dotty.tools.dotc.classpath.{AggregateClassPath, DirectoryClassPath, VirtualDirectoryClassPath}
import dotty.tools.io.{AbstractFile, VirtualDirectory, VirtualFile}
import dotty.tools.io.ClassPath

/** Preloads classpath entries (jars and JRT) into memory as VirtualDirectories.
 *
 *  This avoids repeated I/O and unzipping during compilation, which can
 *  significantly reduce benchmark noise from filesystem operations.
 */
object PreloadedClasspath:
  /** Preload all jars from a classpath string. Returns a ClassPath that can be
   *  used with the compiler. Includes the JRT (Java Runtime) classpath for
   *  java.* classes.
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
    // Include preloaded JRT classpath for java.lang.Object etc.
    val jrtVirtualDir = loadJrtToVirtualDirectory()
    AggregateClassPath((VirtualDirectoryClassPath(jrtVirtualDir) +: classPathEntries).toIndexedSeq)

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

  /** Load the JRT (Java Runtime) filesystem into memory as a VirtualDirectory.
   *  This includes all classes from java.base and other JDK modules.
   */
  private def loadJrtToVirtualDirectory(): VirtualDirectory =
    val root = new VirtualDirectory("jrt", None)
    val fs = FileSystems.getFileSystem(URI.create("jrt:/"))
    val modulesPath = fs.getPath("/modules")

    // Iterate over all modules
    Files.list(modulesPath).iterator().asScala.foreach: modulePath =>
      // Walk all class files in this module
      Files.walk(modulePath).iterator().asScala
        .filter(p => Files.isRegularFile(p) && p.toString.endsWith(".class"))
        .foreach: classPath =>
          // Get relative path within module (e.g., "java/lang/Object.class")
          val relativePath = modulePath.relativize(classPath).toString
          val content = Files.readAllBytes(classPath)

          // Create directory structure
          val parts = relativePath.split("/")
          var currentDir = root
          for part <- parts.init do
            currentDir.lookupName(part, directory = true) match
              case null =>
                currentDir = currentDir.subdirectoryNamed(part).asInstanceOf[VirtualDirectory]
              case dir: VirtualDirectory =>
                currentDir = dir
              case other =>
                throw new IllegalStateException(s"Expected directory but found: $other")

          // Create the file (skip if already exists from another module)
          val fileName = parts.last
          if currentDir.lookupName(fileName, directory = false) == null then
            val file = currentDir.fileNamed(fileName)
            val out = file.output
            try out.write(content)
            finally out.close()
    root
