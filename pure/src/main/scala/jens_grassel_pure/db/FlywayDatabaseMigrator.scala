package jens_grassel_pure.db

import cats.effect.IO
import eu.timepit.refined.auto._
import jens_grassel_pure._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.output.MigrateResult
import org.slf4j.LoggerFactory

class FlywayDatabaseMigrator extends DatabaseMigrator[IO] {

  private val logger = LoggerFactory.getLogger(getClass)

  override def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): IO[MigrateResult] =
    IO {
      Flyway
        .configure()
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .dataSource(url, user, pass)
        .load()
        .migrate()
    }.handleErrorWith { error =>
      logger.error(s"Flyway migration failed for database: $url", error)
      IO.raiseError(new FlywayException(s"Failed to migrate the database at $url", error))
    }
}
