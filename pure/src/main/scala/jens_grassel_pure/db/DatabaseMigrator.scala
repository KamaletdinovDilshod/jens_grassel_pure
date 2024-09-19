package jens_grassel_pure.db

import jens_grassel_pure._
import org.flywaydb.core.api.output.MigrateResult

trait DatabaseMigrator[F[_]] {

  /**
   * Apply pending migrations to the database.
   *
   * @param url
   *   A JDBC database connection url.
   * @param user
   *   The login name for the connection.
   * @param pass
   *   The password for the connection.
   * @return
   *   The number of applied migrations.
   */

  def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): F[MigrateResult]
}
