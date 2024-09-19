package jens_grassel_pure.api

import cats.MonadThrow
import cats.effect._
import cats.implicits._
import fs2.Stream
import io.circe.syntax._
import jens_grassel_pure.db.Repository
import jens_grassel_pure.models._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import play.api.libs.functional.Applicative

final class ProductsRoutes[F[_]: MonadThrow: Concurrent](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product] = jsonOf

  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "products" =>
      val prefix                    = Stream.eval("[".pure[F])
      val suffix                    = Stream.eval("]".pure[F])
      val ps                        = repo
        .loadProducts()
        .groupAdjacentBy(_._1)
        .map { case (_, rows) =>
          Product.fromDatabase(rows.toList)
        }
        .collect { case Some(p) =>
          p
        }
        .map(_.asJson.noSpaces)
        .intersperse(",")
      val result: Stream[F, String] = prefix ++ ps ++ suffix
      Ok(result)

    case req @ POST -> Root / "products" =>
      for {
        p <- req.as[Product]
        _ <- repo.saveProduct(p)
        r <- Ok("Product created successfully")
      } yield r
  }
}
