package jens_grassel_pure.config

import com.typesafe.config.ConfigFactory
import jens_grassel_pure.BaseSpec2
import jens_grassel_pure.config.DatabaseConfigGenerators._
import pureconfig.ConfigSource

class DatabaseConfigTest extends BaseSpec2 {

  "DatabaseConfig" when {
    "loading invalid config format" must {
      "fail" in {
        val config = ConfigFactory.parseString("{}")
        ConfigSource.fromConfig(config).at("database").load[DatabaseConfig] match {
          case Left(_)  => succeed
          case Right(_) => fail("Loading an invalid config must fail!")
        }
      }
    }
    "loading valid config format" when {
      "settings are invalid" must {
        "fail" in {
          forAll("input") { i: Int =>
            val config = ConfigFactory.parseString(
              """database {
                |"driver": "",
                |"url": "",
                |"user": "",
                |"pass": ""
                |""".stripMargin
            )
            ConfigSource.fromConfig(config).at("api").load[ApiConfig] match {
              case Left(_)  => succeed
              case Right(_) => fail("Loading a config with invalid settings must fail!")
            }
          }
        }
      }
      "settings are valid" must {
        "load correct settings" in {
          forAll("input") { expected: DatabaseConfig =>
            val config = ConfigFactory.parseString(
              s"""database {
                 |"driver": "${expected.driver}",
                 |"url": "${expected.url},
                 |"user": "${expected.user},
                 |"pass": "${expected.pass}"
                 |}""".stripMargin
            )
            ConfigSource.fromConfig(config).at("database").load[DatabaseConfig] match {
              case Left(e)  => fail(s"Parsing a valid configuration must succeed!($e)")
              case Right(c) => c must be(expected)
            }
          }
        }
      }
    }
  }
}
