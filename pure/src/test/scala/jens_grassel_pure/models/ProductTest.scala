package jens_grassel_pure.models

import cats.implicits._
import io.circe.parser._
import io.circe.syntax._
import jens_grassel_pure.BaseSpec

class ProductTest extends BaseSpec {
  "Product" when {
    "decoding from JSON" when {
      "JSON format is invalid" must {
        "return an error" in {
          forAll("input") { s: String =>
            decode[Product](s).isLeft must be(true)
          }
        }
      }
      "JSON format is valid" when {
        "data is invalid" must {
          "return an error" in {
            forAll("id", "names") { (id: String, ns: List[String]) =>
              val json = """{"id": """ + id.asJson.noSpaces + """, "names":""" + ns.asJson.noSpaces + """}"""
              decode[Product](json).isLeft must be(true)
            }
          }
        }
        "data is valid" must {
          "return the correct types" in {
            forAll("input") { i: Product =>
              val json =
                s"""{
                   |"id": ${i.id.asJson.noSpaces},
                   |"names": ${i.names.asJson.noSpaces}
                   |}""".stripMargin

              withClue(s"Unable to decode JSON: $json") {
                decode[Product](json) match {
                  case Left(e)  => fail(e.getMessage)
                  case Right(v) =>
                    v.id must be(i.id)
                }
              }
            }
          }
        }
      }
    }
    "encoding to JSON" must {
      "return the correct JSON" in {
        forAll("input") { i: Product =>
          val json = i.asJson.noSpaces
          json must include(s""""id":${i.id.asJson.noSpaces}""")
          json must include(s""""names":${i.names.asJson.noSpaces}""")
        }
      }
      "return decodeable JSON" in {
        forAll("input") { p: Product =>
          decode[Product](p.asJson.noSpaces) match {
            case Left(_)  => fail("Must be able to decode encoded JSON!")
            case Right(d) => withClue("Must decode the same product!")(d.id must be(p.id))
          }
        }
      }
    }
    "#fromDatabase" must {
      "create correct results" in {
        forAll("input") { p: Product =>
          val rows = p.names.toNonEmptyList.map(t => (p.id, t.lang, t.name)).toList
          Product.fromDatabase(rows).map(_.id) must be(p.id.some)
        }
      }
    }
    "ordering" must {
      "sort by ID" in {
        forAll("products") { ps: List[Product] =>
          val expected = ps.map(_.id).sorted
          val sorted   = ps.sorted.map(_.id)
          sorted mustEqual expected
        }
      }
    }
  }
}
