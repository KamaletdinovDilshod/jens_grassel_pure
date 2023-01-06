package jens_grassel_pure

import com.typesafe.config.{Config, ConfigFactory}
import jens_grassel_pure.config.DatabaseConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pureconfig.ConfigReader.Result
import pureconfig.ConfigSource

abstract class BaseSpec2 extends WordSpec
    with MustMatchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  protected val config: Config = ConfigFactory.load
  protected val dbConfig: Result[DatabaseConfig] = ConfigSource.fromConfig(config).at("database").load[DatabaseConfig]

  override def beforeAll(): Unit = {
    val _ = withClue("Database configuration could be loaded!") {
      dbConfig.isRight must be(true)
    }
  }

}
