import sbt.Keys._

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "chen.guo",

  sbtVersion := "0.13.11",
  scalaVersion := "2.11.8",

  test in assembly := {}
)

lazy val testLib = Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test,it" withSources() withJavadoc()
)

lazy val root = Project(id = "root", base = file("."))
  .settings(commonSettings: _*)
  .settings(name := "simple-dag-execution")
  .aggregate(sde, sdeCommon)

lazy val sde = Project(id = "sde", base = file("sde"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.2.1",
      "commons-io" % "commons-io" % "2.5",
      "log4j" % "log4j" % "1.2.17",
      "org.slf4j" % "slf4j-log4j12" % "1.7.21",
      "org.slf4j" % "slf4j-api" % "1.7.21",
      "com.beust" % "jcommander" % "1.48"
    ),
    libraryDependencies ++= testLib
  ).dependsOn(sdeCommon % "compile->compile; test->test; it->it; it->test")


lazy val sdeCommon = Project(id = "sde-common", base = file("sde-common"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= testLib
  )

