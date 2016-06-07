import sbt.Keys._

organization in ThisBuild := "io.scalac"
scalaVersion in ThisBuild := "2.11.8"

lazy val jacksonSettings = Seq(
  scalacOptions in compile += "-parameters",
  libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.3"
)

lazy val calculatorApi =
  (project in file("calculatorApi"))
    .settings(
      version := "1.0-SNAPSHOT",
      libraryDependencies += lagomJavadslJackson
    )
    .dependsOn(utils)

lazy val calculator =
  (project in file("calculator"))
    .settings(
      version := "1.0-SNAPSHOT"
    )
    .enablePlugins(LagomJava)
    .dependsOn(calculatorApi, exchangeRatesApi, utils)
    .settings(jacksonSettings: _*)

lazy val exchangeRatesApi =
  (project in file("exchangeRatesApi"))
    .settings(
      version := "1.0-SNAPSHOT",
      libraryDependencies += lagomJavadslApi,
      libraryDependencies += lagomJavadslJackson
    )
    .dependsOn(utils)

lazy val exchangeRates =
  (project in file("exchangeRates"))
    .enablePlugins(LagomJava)
    .dependsOn(exchangeRatesApi, utils)
    .settings(
      version := "1.0-SNAPSHOT",
      libraryDependencies += lagomJavadslPersistence
    )
    .settings(jacksonSettings: _*)

lazy val utils =
  (project in file("utils"))
    .settings(
      version := "1.0-SNAPSHOT",
      libraryDependencies += lagomJavadslApi
    )
