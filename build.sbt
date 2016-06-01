import sbt.Keys._

name := "Simple-DAG-Execution"
version := "1.0"
scalaVersion := "2.11.8"
sbtVersion := "0.13.11"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val testLib = Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % Test withSources() withJavadoc()
)

lazy val root = Project(id = "root", base = file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.2.1",
      "log4j" % "log4j" % "1.2.17",
      "org.slf4j" % "slf4j-log4j12" % "1.7.21",
      "org.slf4j" % "slf4j-api" % "1.7.21"
    ),
    libraryDependencies ++= testLib
  )


