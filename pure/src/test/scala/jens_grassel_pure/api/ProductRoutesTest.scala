package jens_grassel_pure.api

import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import io.circe.syntax._
import jens_grassel_pure.BaseSpec
import jens_grassel_pure.db._
import jens_grassel_pure.models.{ProductId, _}
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.collection.immutable._

class ProductRoutesTest extends BaseSpec {

  implicit def decodeProduct: EntityDecoder[IO, Product]                   = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf
  private val emptyRepository: Repository[IO]                              = new TestRepository[IO](Seq.empty)

  "ProductRoutes" when {
    "GET / product / ID" when {
      "product does not exist" must {
        val expectedStatusCode = Status.NotFound

        s"return $expectedStatusCode" in {
          forAll("id") { id: ProductId =>
            Uri.fromString("/product/" + id.toString) match {
              case Left(_)  => fail("Could not generate Uri")
              case Right(u) =>
                def service: HttpRoutes[IO]    =
                  Router("/" -> new ProductRoutes(emptyRepository).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result                     = response.unsafeRunSync()
                result.status must be(expectedStatusCode)
                result.body.compile.toVector.unsafeRunSync() must be(empty)
            }
          }
        }
      }
      "product exists" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and the product" in {
          forAll("product") { p: Product =>
            Uri.fromString("/product/" + p.id.toString) match {
              case Left(_)  => fail("Could not generate valid URI!")
              case Right(u) =>
                val repo: Repository[IO]       = new TestRepository[IO](Seq(p))
                def service: HttpRoutes[IO]    =
                  Router("/" -> new ProductRoutes(repo).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result                     = response.unsafeRunSync
                result.status must be(expectedStatusCode)
                result.as[Product].unsafeRunSync().id must be(p.id)
            }
          }
        }
      }
    }
    "Put / product /ID" when {
      "request body is invalid" must {
        val expectedStatusCode = Status.BadRequest

        s"return $expectedStatusCode" in {
          forAll("id") { id: ProductId =>
            Uri.fromString("/product/" + id.toString) match {
              case Left(_)  => fail("Could not generate valid URI!")
              case Right(u) =>
                def service: HttpRoutes[IO]    =
                  Router("/" -> new ProductRoutes(emptyRepository).routes)
                val payload                    = scala.util.Random.alphanumeric.take(256).mkString
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.PUT, uri = u)
                    .withEntity(payload.asJson.noSpaces)
                )
                val result                     = response.unsafeRunSync()
                result.status must be(expectedStatusCode)
                result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
      }
      "request body is valid" when {
        "product does not exist" must {
          val expectedStatusCode = Status.NotFound

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              Uri.fromString("/product/" + p.id.toString) match {
                case Left(_)  => fail("Could not generate valid URI!")
                case Right(u) =>
                  def service: HttpRoutes[IO]    =
                    Router("/" -> new ProductRoutes(emptyRepository).routes)
                  val response: IO[Response[IO]] = service.orNotFound.run(
                    Request(method = Method.PUT, uri = u)
                      .withEntity(p)
                  )
                  val result                     = response.unsafeRunSync
                  result.status must be(expectedStatusCode)
                  result.body.compile.toVector.unsafeRunSync must be(empty)
              }
            }
          }
        }
        "product exists" must {
          val expectedStatusCode = Status.NoContent

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              Uri.fromString("/product/" + p.id.toString) match {
                case Left(_)  => fail("Could not generate valid URI!")
                case Right(u) =>
                  val repo: Repository[IO]    = new TestRepository[IO](Seq(p))
                  def service: HttpRoutes[IO] =
                    Router("/" -> new ProductRoutes(repo).routes)

                  val response: IO[Response[IO]] = service.orNotFound.run(
                    Request(method = Method.PUT, uri = u)
                      .withEntity(p)
                  )
                  val result                     = response.unsafeRunSync()
                  result.status must be(expectedStatusCode)
                  result.body.compile.toVector.unsafeRunSync() must be(empty)
              }
            }
          }
        }
      }
    }
  }
}
