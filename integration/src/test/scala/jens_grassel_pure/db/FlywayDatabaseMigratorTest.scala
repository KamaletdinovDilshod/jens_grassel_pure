package jens_grassel_pure.db

import cats.effect._
import cats.effect.unsafe.implicits.global
import eu.timepit.refined.pureconfig._ // DON'T REMOVE THIS
import eu.timepit.refined.auto._
import jens_grassel_pure.BaseSpec2
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException

final class FlywayDatabaseMigratorTest extends BaseSpec2 {

  override def beforeEach(): Unit =
    dbConfig.foreach { cfg =>
      val flyway: Flyway = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.pass)
        .cleanDisabled(false)
        .load()
      val _              = flyway.migrate()
      flyway.clean()
    }

  override def afterEach(): Unit =
    dbConfig.foreach { cfg =>
      val flyway: Flyway = Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.pass)
        .cleanDisabled(false)
        .load()
      flyway.clean()
    }

  "FlywayDatabaseMigrator#migrate" when {
    "the database is configured and available" when {
      "the database is not up to date" must {
        "return the number of applied migrations" in {
          dbConfig.map { cfg =>
            val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
            val program                        = migrator.migrate(cfg.url, cfg.user, cfg.pass)
            program.unsafeRunSync.migrationsExecuted must be > 0
          }
        }
      }

      "the database is up to date" must {
        "return zero" in {
          dbConfig.map { cfg =>
            val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
            val program                        = migrator.migrate(cfg.url, cfg.user, cfg.pass)
            val _                              = program.unsafeRunSync
            program.unsafeRunSync.migrationsExecuted must be(0)
          }
        }
      }
    }

    "the database is not available" must {
      "throw an exception" in {
        dbConfig.map { cfg =>
          val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
          val program                        = migrator.migrate(cfg.url, "fakeuser", cfg.pass)
          an[FlywayException] must be thrownBy program.unsafeRunSync
        }
      }
    }
  }

}
