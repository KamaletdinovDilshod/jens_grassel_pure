import sbt.*
import sbt.Keys.*
import sbt.ThisBuild
import sbt.Package
import Dependencies.*

// Общие настройки
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

// =======================================================
// ROOT PROJECT (aggregate only, NOT runnable)
// =======================================================
lazy val root = (project in file("."))
  .aggregate(
    pure,
    integration
  )
  .settings(
    name := "jens_grassel_pure",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    addCommandAlias("run", "pure/run"),

    // root не публикуем и не пакуем как приложение
    publish / skip              := true,
    Compile / run / fork        := true,
    Compile / packageBin / skip := true
  )

// =======================================================
// PURE MODULE (APPLICATION ENTRYPOINT)
// =======================================================
lazy val pure = (project in file("pure"))
  .enablePlugins(JavaAppPackaging, JDKPackagerPlugin)
  .settings(
    name := "pure",

    libraryDependencies ++= rootDependencies,

    Compile / run / fork := true,

    // Предполагаем, что у тебя есть:
    // package jens_grassel_pure
    // object Pure extends App / IOApp ...
    Compile / mainClass := Some("jens_grassel_pure.Pure"),

    // Добавляем Main-Class в MANIFEST.MF JAR-а,
    // чтобы java -jar знал, что запускать
    Compile / packageBin / packageOptions +=
      Package.MainClass("jens_grassel_pure.Pure"),

    // Настройки native-packager (для stage / universal)
    Universal / packageName       := "pure",
    Universal / topLevelDirectory := None
  )

// =======================================================
// INTEGRATION MODULE
// =======================================================
lazy val integration = (project in file("integration"))
  .dependsOn(pure % "test->test")
  .settings(
    publish / skip           := true,
    Test / parallelExecution := false,
    libraryDependencies ++= integrationDependencies
  )
