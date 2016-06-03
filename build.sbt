import sbt.Keys._

lazy val appVersion = "1.0"

lazy val appSettings = Seq(
  name := "simple-dag-execution",
  version := appVersion
)

scalaVersion := "2.11.8"
sbtVersion := "0.13.11"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val testLib = Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test,it" withSources() withJavadoc()
)

lazy val root = Project(id = "root", base = file("."))
  .settings(appSettings: _*)
  .aggregate(sde, sdeCommon)

lazy val sde = Project(id = "sde", base = file("sde"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    version := appVersion,
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.2.1",
      "log4j" % "log4j" % "1.2.17",
      "org.slf4j" % "slf4j-log4j12" % "1.7.21",
      "org.slf4j" % "slf4j-api" % "1.7.21"
    ),
    libraryDependencies ++= testLib
  ).dependsOn(sdeCommon % "compile->compile; test->test; it->it")


lazy val sdeCommon = Project(id = "sde-common", base = file("sde-common"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    version := appVersion,
    libraryDependencies ++= testLib
  )

