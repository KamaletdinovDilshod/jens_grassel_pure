package jens_grassel_pure.config

import eu.timepit.refined.auto._       // DON'T REMOVE IT
import eu.timepit.refined.pureconfig._ // DON'T REMOVE IT
import jens_grassel_pure.models._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto._

final case class DatabaseConfig(
    driver: NonEmptyString,
    url: DatabaseUrl,
    user: DatabaseLogin,
    pass: DatabasePassword
)

object DatabaseConfig {
  implicit val dbConfigReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]
}
