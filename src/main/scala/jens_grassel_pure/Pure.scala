package jens_grassel_pure

import cats.effect._
import cats.effect.unsafe.implicits._
import cats.implicits._
import com.typesafe.config._
import doobie._
import eu.timepit.refined.auto._
import jens_grassel_pure.api._
import jens_grassel_pure.config._
import jens_grassel_pure.db._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Pure extends IOApp {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def run(args: List[String]): IO[ExitCode] = {
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val cfg       = ConfigFactory.load
    val apiConfig = ConfigSource.fromConfig(cfg).at("api").loadOrThrow[ApiConfig]
    val dbConfig  = ConfigSource.fromConfig(cfg).at("database").loadOrThrow[DatabaseConfig]

    val program: IO[IO[ExitCode]] = for {
      _ <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      tx = Transactor
        .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass)

      repo           = new DoobieRepository(tx)
      productRoutes  = new ProductRoutes(repo)
      productsRoutes = new ProductsRoutes(repo)
      routes         = productRoutes.routes <+> productsRoutes.routes
      httpApp        = Router("/" -> routes).orNotFound
      server = BlazeServerBuilder[IO](ExecutionContext.Implicits.global)
        .bindHttp(apiConfig.port, apiConfig.host)
        .withHttpApp(httpApp)
      fiber = server.resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
    } yield fiber

    program.attempt.unsafeRunSync match {
      case Left(e) =>
        IO {
          println("*** An error occured! ***")
          if (e != null) {
            println(e.getMessage)
          }
          ExitCode.Error
        }
      case Right(r) => r
    }
  }
}
