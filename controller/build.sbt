ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

// Define versions of used dependencies
val akkaVersion: String = "2.8.0"
val akkaHttpVersion: String = "10.5.0"
val logbackVersion: String = "1.4.7"
val slickVersion: String = "3.4.1"
val postgresqlVersion: String = "42.5.4"
val slickHikaricpVersion: String = "3.4.1"

// Add dependencies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.postgresql" % "postgresql" % postgresqlVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickHikaricpVersion
)

// Add option to allow server to keep running after main method finishes
fork := true

lazy val root = (project in file("."))
  .settings(
    name := "controller",
    idePackagePrefix := Some("rs.ac.bg.etf.jj203218m.controller")
  )
