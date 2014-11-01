name := """stormtest"""

version := "1.0"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")


//////
// Repositories
//////

resolvers += "maven-central" at "http://repo1.maven.org/maven2/"

resolvers += "clojars" at "https://clojars.org/repo"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "clojure-releases" at "http://build.clojure.org/releases"

resolvers += "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/"

resolvers += "Scala-sbt plugin repo" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"

resolvers += "sbt-assembly-resolver-0" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"

//////
// Dependencies
//////

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" % "scalatest" % "1.4.RC2"

libraryDependencies += "io.spray" % "spray-json_2.11" % "1.3.0"

libraryDependencies += "com.github.velvia" % "scala-storm_2.11" % "0.2.4-SNAPSHOT"

libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.1-incubating" % "compile" exclude("junit", "junit")

libraryDependencies += "org.zeromq" % "jeromq" % "0.3.4"

libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.2"

libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "2.1.2"

// scala-ized cassandra layer on top of datastax, highly dependent on finagle
// libraryDependencies += "com.websudos" %% "phantom-dsl" % "1.2.2"

// dont need right now, but...
// libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"


//////
// Paths
//////

// un-managed libraries lie in ./lib
unmanagedBase := baseDirectory.value / "lib"


//////
// JVM and other configs
//////

// This is to prevent error [java.lang.OutOfMemoryError: PermGen space]
// javaOptions += "-XX:MaxPermSize=1g"

// javaOptions += "-Xmx2g"

// scalacOptions += "-Yresolve-term-conflict:package"

// force scalaVersion
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

fork := true

exportJars := true


