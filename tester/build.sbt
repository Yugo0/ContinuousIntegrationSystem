ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

// Define versions of used dependencies
val akkaVersion: String = "2.8.0"
val akkaHttpVersion: String = "10.5.0"
val logbackVersion: String = "1.4.7"
val jgitVersion: String = "6.3.0.202209071007-r"
val scalaCompilerVersion: String = "2.13.10"
val courierVersion: String = "3.2.0"

// Add dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.eclipse.jgit" % "org.eclipse.jgit" % jgitVersion,
  "org.scala-lang" % "scala-compiler" % scalaCompilerVersion,
  "com.github.daddykotex" %% "courier" % courierVersion
)

// Add option to allow server to keep running after main method finishes
fork := true

lazy val root = (project in file("."))
  .settings(
    name := "tester",
    idePackagePrefix := Some("rs.ac.bg.etf.jj203218m.tester")
  )
