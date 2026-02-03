#!/usr/bin/env -S scala shebang

val url = "https://repo1.maven.org/maven2/org/scala-lang/scala3-compiler_3/maven-metadata.xml"
val xml = scala.io.Source.fromURL(url).mkString
val nightlyPattern = """<version>(.+-NIGHTLY)</version>""".r
val nightlies = nightlyPattern.findAllMatchIn(xml).map(_.group(1)).toSeq
println(nightlies.last)
