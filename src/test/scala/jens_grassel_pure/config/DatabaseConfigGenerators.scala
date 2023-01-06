package jens_grassel_pure.config

import eu.timepit.refined.api.RefType
import jens_grassel_pure.models.DatabasePassword
import eu.timepit.refined.auto._
import org.scalacheck._

object DatabaseConfigGenerators {

  val DefaultPassword: DatabasePassword = "secret"

  val genDatabaseConfig: Gen[DatabaseConfig] = for {
    gp <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    p = RefType.applyRef[DatabasePassword](gp.mkString).getOrElse(DefaultPassword)
  } yield DatabaseConfig(
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5422/test-database",
    user = "pure",
    pass = p
  )

  implicit val arbitraryDatabaseConfig: Arbitrary[DatabaseConfig] = Arbitrary(genDatabaseConfig)
}
