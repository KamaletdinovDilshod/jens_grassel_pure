package jens_grassel_pure.api

import cats._
import cats.effect._
import cats.Applicative
import cats.effect.unsafe.implicits.global
import io.circe.syntax.EncoderOps
import jens_grassel_pure.BaseSpec2
import jens_grassel_pure.db.TestRepository
import jens_grassel_pure.db._
import jens_grassel_pure.models.TypeGenerators2._
import jens_grassel_pure.models._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

class ProductsRoutesTest extends BaseSpec2 {
  implicit def decodeProduct: EntityDecoder[IO, Product]                   = jsonOf
  implicit def decodeProducts: EntityDecoder[IO, List[Product]]            = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf
  private val emptyRepository: Repository[IO]                              = new TestRepository[IO](Seq.empty)

  "ProductsRoutes" when {
    "GET/product" when {
      "no products exist" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and an empty list" in {
          def service: HttpRoutes[IO] =
            Router("/" -> new ProductsRoutes(emptyRepository).routes)
          val response: IO[Response[IO]] = service.orNotFound.run(
//              """use uri"" string interpolation instead""", "0.20"
            Request(method = Method.GET, uri = Uri.uri("/products"))
          )
          val result = response.unsafeRunSync
          result.status must be(expectedStatusCode)
          result.as[List[Product]].unsafeRunSync mustEqual List.empty[Product]
        }
      }
      "products exist" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and a list of products" in {
          forAll("products") { ps: List[Product] =>
            val repo: Repository[IO] = new TestRepository[IO](ps)
            def service: HttpRoutes[IO] =
              Router("/" -> new ProductsRoutes(repo).routes)
            val response: IO[Response[IO]] = service.orNotFound.run(
              Request(method = Method.GET, uri = Uri.uri("/products"))
            )
            val result = response.unsafeRunSync
            result.status must be(expectedStatusCode)
            result.as[List[Product]].unsafeRunSync mustEqual ps
          }
        }
      }
    }

    "POST /products" when {
      "request body is invalid" must {
        val expectedStatusCode = Status.BadRequest

        s"return $expectedStatusCode" in {
          def service: HttpRoutes[IO] =
            Router("/" -> new ProductsRoutes(emptyRepository).routes)
          val payload = scala.util.Random.alphanumeric.take(256).mkString
          val response: IO[Response[IO]] = service.orNotFound.run(
            Request(method = Method.POST, uri = Uri.uri("/products"))
              .withEntity(payload.asJson.noSpaces)
          )
          val result = response.unsafeRunSync
          result.status must be(expectedStatusCode)
          result.body.compile.toVector.unsafeRunSync must be(empty)
        }
      }

      "request body is valid" when {
        "product could be saved" must {
          val expectedStatusCode = Status.NoContent

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              val repo: Repository[IO] = new TestRepository[IO](Seq(p))
              def service: HttpRoutes[IO] =
                Router("/" -> new ProductsRoutes(repo).routes)
              val response: IO[Response[IO]] = service.orNotFound.run(
                Request(method = Method.POST, uri = Uri.uri("/products"))
                  .withEntity(p)
              )

              val result = response.unsafeRunSync()
              result.status must be(expectedStatusCode)
              result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
        "product could not be saved" must {
          val expectedStatusCode = Status.InternalServerError

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              def service: HttpRoutes[IO] =
                Router("/" -> new ProductsRoutes(emptyRepository).routes)

              val response: IO[Response[IO]] = service.orNotFound.run(
                Request(method = Method.POST, uri = Uri.uri("/products"))
                  .withEntity(p)
              )
              val result = response.unsafeRunSync
              result.status must be(expectedStatusCode)
              result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }

      }
    }
  }
}
