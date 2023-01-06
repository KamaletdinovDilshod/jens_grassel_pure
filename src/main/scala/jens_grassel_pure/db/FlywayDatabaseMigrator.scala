package jens_grassel_pure.db


import cats.effect.IO
import jens_grassel_pure.models.{DatabaseLogin, DatabasePassword, DatabaseUrl}
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway


class FlywayDatabaseMigrator extends DatabaseMigrator[IO] {
  override def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): IO[Int] =
    IO {
      val flyway: Flyway = Flyway.configure().baselineOnMigrate(true).dataSource(url, user, pass).load()
      flyway.migrate()
    }
}
