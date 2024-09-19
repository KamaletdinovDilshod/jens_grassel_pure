package jens_grassel_pure.config

import jens_grassel_pure._
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import eu.timepit.refined.pureconfig._ // DON'T REMOVE THIS LINE

/**
 * The configuration for our HTTP API.
 *
 * @param host
 *   The hostname or ip address on which the service shall listen.
 * @param port
 *   The port number on which the service shall listen.
 */

final case class ApiConfig(host: NonEmptyString, port: PortNumber)
object ApiConfig {
  implicit val dbConfigReader: ConfigReader[ApiConfig] = deriveReader[ApiConfig]
}
