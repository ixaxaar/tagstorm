import AssemblyKeys._ // put this at the top of the file


/////
// Jar packaging settings
/////
assemblySettings

// exclude these jars from the production package
excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter { jar =>
    jar.data.getName == "scalatest-1.4.RC2.jar" ||
    jar.data.getName == "storm-core-0.9.1-incubating.jar"
  }
}

jarName in assembly := "stats.jar"

mainClass in assembly := Some("stats.main")
