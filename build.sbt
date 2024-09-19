import Dependencies.*
import sbt.ThisBuild

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "KD_JensGrassel",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )
  .aggregate(
    pure,
    integration
  )
  .settings(
    addCommandAlias("run", "pure/run")
  )

lazy val integration = (project in file("integration"))
  .settings(
    publish / skip           := true,
    Test / parallelExecution := false,
    libraryDependencies ++= integrationDependencies
  )
  .dependsOn(
    pure % "test->test"
  )

lazy val pure = (project in file("pure"))
  .settings(
    name                 := "pure",
    libraryDependencies ++= rootDependencies,
    Compile / run / fork := true
  )
