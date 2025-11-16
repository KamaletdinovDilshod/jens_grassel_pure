import Dependencies.*
import sbt.ThisBuild
import sbtassembly.AssemblyPlugin.autoImport.*

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

// ========= COMMON MERGE STRATEGY FOR ASSEMBLY =========
ThisBuild / assemblyMergeStrategy := {
  case PathList("module-info.class") =>
    MergeStrategy.discard

  case PathList("META-INF", xs @ _*) =>
    xs.map(_.toLowerCase) match {
      case "manifest.mf" :: Nil  => MergeStrategy.discard
      case "index.list" :: Nil   => MergeStrategy.discard
      case "dependencies" :: Nil => MergeStrategy.discard
      case _                     => MergeStrategy.discard
    }

  case x if x.endsWith("module-info.class") => MergeStrategy.discard
  case _                                    => MergeStrategy.first
}

// =======================================================

lazy val root = (project in file("."))
  .settings(
    name := "jens_grassel_pure",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    addCommandAlias("run", "pure/run"),
    publish / skip := true
  )
  .aggregate(
    pure,
    integration
  )

lazy val integration = (project in file("integration"))
  .settings(
    publish / skip := true,
    Test / parallelExecution := false,
    libraryDependencies ++= integrationDependencies
  )
  .dependsOn(
    pure % "test->test"
  )

lazy val pure = (project in file("pure"))
  .settings(
    name := "pure",
    libraryDependencies ++= rootDependencies,
    Compile / run / fork := true
  )
