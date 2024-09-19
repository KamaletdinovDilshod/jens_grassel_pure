package jens_grassel_pure

import com.typesafe.config.{Config, ConfigFactory}
import jens_grassel_pure.config.DatabaseConfig
import jens_grassel_pure.models.TypeGenerators2
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import pureconfig.ConfigReader.Result
import pureconfig.ConfigSource

abstract class BaseSpec2
  extends AnyWordSpec
  with Matchers
  with ScalaCheckDrivenPropertyChecks
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with TypeGenerators2 {

  protected val config: Config                   = ConfigFactory.load
  protected val dbConfig: Result[DatabaseConfig] = ConfigSource.fromConfig(config).at("database").load[DatabaseConfig]

  override def beforeAll(): Unit = {
    val _ = withClue("Database configuration could be loaded!") {
      dbConfig.isRight must be(true)
    }
  }

}
