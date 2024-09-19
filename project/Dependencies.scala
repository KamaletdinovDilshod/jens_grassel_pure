import sbt.*

object Dependencies {

  object Versions {
    val akka                    = "2.8.6"
    val akkaHttpJsonSerializers = "1.39.2"
    val circe                   = "0.14.9"
    val cats                    = "2.12.0"
    val catsEffect              = "3.5.4"
    val akkaHttp                = "10.5.3"
    val http4s                  = "0.23.27"
    val fs2                     = "3.10.2"
    val doobie                  = "1.0.0-RC5"
    val newtype                 = "0.4.4"
    val pureConfig              = "0.17.7"
    val refinedCore             = "0.11.1"
    val refined                 = "0.11.2"
    val slick                   = "3.5.0"
    val slickHikari             = "3.5.1"
    val postgresql              = "42.7.3"
    val slickPg                 = "0.22.2"
    val flyway                  = "10.15.2"
    val scalacheck              = "1.18.1"
    val scalatest               = "3.2.19"
    val kittens                 = "3.3.0"
  }

  object Libraries {
    def http4s(artifact: String): ModuleID                        = "org.http4s"        %% s"http4s-$artifact" % Versions.http4s
    def circe(artifact: String): ModuleID                         = "io.circe"          %% artifact            % Versions.circe
    def doobie(artifact: String): ModuleID                        = "org.tpolecat"      %% s"doobie-$artifact" % Versions.doobie
    def akkaTypeSafe(artifact: String, version: String): ModuleID = "com.typesafe.akka" %% s"akka-$artifact"   % version

    val cats          = "org.typelevel" %% "cats-core"          % Versions.cats
    val catsEffect    = "org.typelevel" %% "cats-effect"        % Versions.catsEffect
    val catsKernel    = "org.typelevel" %% "cats-effect-kernel" % Versions.catsEffect
    val catsKernelStd = "org.typelevel" %% "cats-effect-std"    % Versions.catsEffect
    val kittens       = "org.typelevel" %% "kittens"            % Versions.kittens

    val circeCore    = circe("circe-core")
    val circeGeneric = circe("circe-generic")
    val circeParser  = circe("circe-parser")
    val circeRefined = circe("circe-refined")

    val refinedCore       = "eu.timepit" %% "refined"            % Versions.refinedCore
    val refinedCats       = "eu.timepit" %% "refined-cats"       % Versions.refined
    val refinedPureconfig = "eu.timepit" %% "refined-pureconfig" % Versions.refined

    val slick           = "com.typesafe.slick"  %% "slick"              % Versions.slick
    val slickHikaricp   = "com.typesafe.slick"  %% "slick-hikaricp"     % Versions.slickHikari
    val slickPg         = "com.github.tminglei" %% "slick-pg"           % Versions.slickPg
    val slickPgPlayJson = "com.github.tminglei" %% "slick-pg_play-json" % Versions.slickPg

    val postgresql = "org.postgresql" % "postgresql" % Versions.postgresql

    val flyway         = "org.flywaydb" % "flyway-core"                % "10.15.2"
    val flywayPostgres = "org.flywaydb" % "flyway-database-postgresql" % "10.15.2"

    val pureConfig = "com.github.pureconfig" %% "pureconfig" % Versions.pureConfig

    val fs2Core = "co.fs2" %% "fs2-core" % Versions.fs2
    val fs2Io   = "co.fs2" %% "fs2-io"   % Versions.fs2

    val logback = "ch.qos.logback" % "logback-classic" % "1.5.6" % Runtime
    val slf4j = "org.slf4j" % "slf4j-api" % "2.0.12"

    val doobieCore     = doobie("core")
    val doobieHikari   = doobie("hikari")   // HikariCP transactor.
    val doobiePostgres = doobie("postgres") // Postgres driver 42.3.1 + type mappings.
    val doobieRefined  = doobie("refined")  // Support Refined Types

    val newtype = "io.estatico" %% "newtype" % Versions.newtype

    val http4sDsl    = http4s("dsl")
    val http4sServer = http4s("ember-server")
    val http4sClient = http4s("ember-client")
    val http4sCirce  = http4s("circe")

    val scalacheck    = "org.scalacheck"    %% "scalacheck"      % Versions.scalacheck % "test"
    val scalatest     = "org.scalatest"     %% "scalatest"       % Versions.scalatest
    val scalatestPlus = "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0"          % Test
  }

  lazy val rootDependencies: Seq[ModuleID] = Seq(
    Libraries.cats,
    Libraries.catsEffect,
    Libraries.catsKernel,
    Libraries.catsKernelStd,
    Libraries.kittens,
    Libraries.circeCore,
    Libraries.circeGeneric,
    Libraries.circeParser,
    Libraries.circeRefined,
    Libraries.refinedCore,
    Libraries.refinedCats,
    Libraries.refinedPureconfig,
    Libraries.slick,
    Libraries.slickHikaricp,
    Libraries.slickPg,
    Libraries.slickPgPlayJson,
    Libraries.postgresql,
    Libraries.flyway,
    Libraries.pureConfig,
    Libraries.fs2Core,
    Libraries.fs2Io,
    Libraries.doobieCore,
    Libraries.doobieHikari,
    Libraries.doobiePostgres,
    Libraries.doobieRefined,
    Libraries.newtype,
    Libraries.http4sDsl,
    Libraries.http4sServer,
    Libraries.http4sClient,
    Libraries.http4sCirce,
    Libraries.scalacheck,
    Libraries.scalatest,
    Libraries.scalatestPlus,
    Libraries.flywayPostgres,
    Libraries.logback,
    Libraries.slf4j
  )

  val integrationDependencies: Seq[ModuleID] = Seq(
    Libraries.pureConfig,
    Libraries.scalacheck,
    Libraries.scalatest,
    Libraries.scalatestPlus,
    Libraries.cats,
    Libraries.catsEffect
  )
}
