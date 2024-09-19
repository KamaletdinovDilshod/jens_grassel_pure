package jens_grassel_pure

import cats.effect._
import cats.implicits._
import com.comcast.ip4s.{IpAddress, Port}
import com.typesafe.config._
import doobie.util.transactor.Transactor
import eu.timepit.refined.auto._
import jens_grassel_pure.api._
import jens_grassel_pure.config._
import jens_grassel_pure.db._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.defaults.Banner
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object Pure extends IOApp.Simple {

  override def run: IO[Unit] = {

    def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
      Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

    implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val (apiConfig, dbConfig): (ApiConfig, DatabaseConfig) = {
      val cfg = ConfigFactory.load(getClass.getClassLoader)
      (
        ConfigSource.fromConfig(cfg).at("api").loadOrThrow[ApiConfig],
        ConfigSource.fromConfig(cfg).at("database").loadOrThrow[DatabaseConfig]
      )
    }

    val program = for {
      _             <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass).toResource
      tx             = Transactor
                         .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass, None)
      repo           = new DoobieRepository(tx)
      productRoutes  = new ProductRoutes(repo)
      productsRoutes = new ProductsRoutes(repo)
      routes         = productRoutes.routes <+> productsRoutes.routes
      httpApp        = Router("/" -> routes).orNotFound
      server        <- (
                         IpAddress.fromString(apiConfig.host),
                         Port.fromInt(apiConfig.port)
                       ).mapN { (host, port) =>
                         EmberServerBuilder
                           .default[IO]
                           .withHost(host)
                           .withPort(port)
                           .withHttpApp(httpApp)
                           .build
                           .evalTap(showEmberBanner[IO])
                       }.getOrElse(
                         Resource.raiseError[IO, Server, Throwable](new Throwable("Could not load server configurations"))
                       )
    } yield server

    program.useForever
  }
}
