name := "propertynder"

version := "1.0"

scalaVersion := "2.11.8"

// Linear lagebra
libraryDependencies ++=
  Seq("breeze",
    "breeze-natives",
    "breeze-viz"
  ).map("org.scalanlp" %% _ % "0.13")

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

// Test
val scalaTestVersion = "3.0.1"
libraryDependencies += "org.scalactic" %% "scalactic" % scalaTestVersion
libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

// Akka
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.17"
libraryDependencies ++= Seq("akka-http-core", "akka-http", "akka-http-xml").map(
  "com.typesafe.akka" %% _ % "10.0.4"
)

//Logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

// Cats
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"

