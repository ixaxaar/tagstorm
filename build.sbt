name := """stormtest"""

version := "1.0"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

//////
// Repositories
//////

resolvers += "maven-central" at "http://repo1.maven.org/maven2/"

resolvers += "clojars" at "https://clojars.org/repo"

resolvers += "clojure-releases" at "http://build.clojure.org/releases"

//////
// Dependencies
//////

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" % "scalatest" % "1.4.RC2"

libraryDependencies += "io.spray" % "spray-json_2.11" % "1.3.0"

// libraryDependencies += "com.github.velvia" % "scala-storm_2.9.1" % "0.2.2"
libraryDependencies += "com.github.velvia" % "scala-storm_2.11" % "0.2.4-SNAPSHOT"

libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.2-incubating" % "compile" exclude("junit", "junit")

// dont forget to export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
// libraryDependencies += "org.zeromq" % "jzmq" % "3.1.0"
libraryDependencies += "org.zeromq" % "jeromq" % "0.3.4"

// libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

// for using storm directly
// libraryDependencies += "org.apache.storm" % "storm" % "0.9.2-incubating" % "compile"

//////
// Paths
//////

// un-managed libraries lie in ./lib
unmanagedBase := baseDirectory.value / "lib"

// force scalaVersion
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
