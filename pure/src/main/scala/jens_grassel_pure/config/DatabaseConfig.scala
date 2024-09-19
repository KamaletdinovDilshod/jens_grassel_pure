package jens_grassel_pure.config

import jens_grassel_pure._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto._
import eu.timepit.refined.pureconfig._ // DON'T REMOVE THIS LINE

/**
 * The configuration for our database connection.
 *
 * @param driver
 *   The class name of the driver to use.
 * @param url
 *   The JDBC connection url (driver specific).
 * @param user
 *   The username for the database connection.
 * @param pass
 *   The password for the database connection.
 */

final case class DatabaseConfig(
  driver: NonEmptyString,
  url: DatabaseUrl,
  user: DatabaseLogin,
  pass: DatabasePassword
)

object DatabaseConfig {
  implicit val dbConfigReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]
}
