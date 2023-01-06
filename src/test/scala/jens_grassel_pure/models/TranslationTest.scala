package jens_grassel_pure.models

import jens_grassel_pure.BaseSpec2
import io.circe.parser._
import io.circe.syntax._
import eu.timepit.refined.api.RefType
import io.circe.refined.refinedEncoder
import jens_grassel_pure.models.TypeGenerators2._

class TranslationTest extends BaseSpec2 {
  "Translation" when {
    "Decoding from JSON" when {
      "JSON format is invalid" must {
        "return an error" in {
          forAll("input") { s: String =>
            decode[Translation](s).isLeft must be(true)
          }
        }
      }
      "JSON format is valid" when {
        "data is invalid" must {
          "return an error" in {
            forAll("lang", "name") { (l: String, n: String) =>
              whenever(
                RefType
                  .applyRef[LanguageCode](l)
                  .toOption
                  .isEmpty || RefType.applyRef[ProductName](n).toOption.isEmpty
              ) {
                val json = """{"lang": """ + l.asJson.noSpaces + """, "name": """ + n.asJson.noSpaces + """}"""
                decode[Translation](json).isLeft must be(true)
              }
            }
          }
        }
        "data is valid" must {
          "return the correct types" in {
            forAll("input") { i: Translation =>
              val json =
                s"""{
                   |"lang": ${i.lang.asJson.noSpaces}
                   |"name": ${i.name.asJson.noSpaces}
                   |""".stripMargin
              withClue(s"Unable to decode JSON: $json") {
                decode[Translation](json) match {
                  case Left(e)  => fail(e.getMessage)
                  case Right(v) => v must be(i)
                }
              }
            }
          }
        }
      }
    }

    "encoding to JSON" must {
      "return correct JSON" in {
        forAll("input") { i: Translation =>
          val json = i.asJson.noSpaces
          json must include(s""""lang": ${i.lang.asJson.noSpaces}""")
          json must include(s""""name": ${i.name.asJson.noSpaces}""")
        }
      }
      "return decodable JSON" in {
        forAll("input") { i: Translation =>
          decode[Translation](i.asJson.noSpaces) match {
            case Left(_)  => fail("Must be able to decode encoded JSON!")
            case Right(d) => withClue("Must be the same product!")(d must be(i))
          }
        }
      }
    }
  }
}
