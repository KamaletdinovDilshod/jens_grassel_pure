package jens_grassel_pure.api

import cats.effect._
import cats.implicits._
//import eu.timepit.refined.auto._
import jens_grassel_pure.db.Repository
import jens_grassel_pure.models.Product
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._


final class ProductRoutes[F[_]: Async](repo: Repository[F]) extends Http4sDsl[F] {

  implicit def decodeProduct: EntityDecoder[F, Product] = jsonOf

  implicit def encodeProduct[A[_]]: EntityEncoder[A, Product] = jsonEncoderOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "product" / UUIDVar(id) =>
      for {
        rows <- repo.loadProduct(id)
        resp <- Product.fromDatabase(rows).fold(NotFound())(p => Ok(p))
      } yield resp

    case req @ PUT -> Root / "product" / UUIDVar(_) =>
      req
        .as[Product]
        .flatMap { p =>
          for {
            cnt <- repo.updateProduct(p)
            res <- cnt match {
              case 0 => NotFound()
              case _ => NoContent()
            }
          } yield res
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }
  }
}
