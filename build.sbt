name := """stormtest"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" % "scalatest" % "1.4.RC2"

libraryDependencies += "io.spray" % "spray-json_2.11" % "1.3.0"

libraryDependencies += "com.github.velvia" % "scala-storm_2.9.1" % "0.2.2"

// For using Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

// for using storm directly
// libraryDependencies += "org.apache.storm" % "storm" % "latest.integration"

// add maven repos
resolvers += "maven-central" at "http://repo1.maven.org/maven2/"

// add clojure repos

resolvers += "clojars" at "https://clojars.org/repo"

resolvers += "clojure-releases" at "http://build.clojure.org/releases"

// un-managed libraries lie in ./lib
unmanagedBase := baseDirectory.value / "lib"
