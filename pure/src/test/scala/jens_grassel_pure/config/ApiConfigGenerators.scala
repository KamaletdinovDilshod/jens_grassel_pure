package jens_grassel_pure.config

import eu.timepit.refined.api.RefType
import eu.timepit.refined.auto._
import jens_grassel_pure._
import org.scalacheck._

object ApiConfigGenerators {
  val DefaultHost: NonEmptyString = "api.exmple.com"
  val DefaultPort: PortNumber     = 1234

  val genApiConfig: Gen[ApiConfig] = for {
    gh <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    gp <- Gen.choose(1, 65535)
    h   = RefType.applyRef[NonEmptyString](gh.mkString).getOrElse(DefaultHost)
    p   = RefType.applyRef[PortNumber](gp).getOrElse(DefaultPort)
  } yield ApiConfig(host = h, port = p)

  implicit val arbitraryApiConfig: Arbitrary[ApiConfig] = Arbitrary(genApiConfig)

}
