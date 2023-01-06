package jens_grassel_pure.models

import cats.Order
import cats.data.NonEmptySet
import cats.implicits._
import io.circe._
import io.circe.generic.semiauto._


final case class Product(id: ProductId, names: NonEmptySet[Translation])

object Product {
  implicit val decode: Decoder[Product] = deriveDecoder[Product]
  implicit val encode: Encoder[Product] = deriveEncoder[Product]

  implicit val order: Order[Product] = new Order[Product] {
    def compare(x: Product, y: Product): Int = x.id.compare(y.id)
  }

  def merge(ps: List[Product])(p: Product): List[Product] = {
    ps.headOption.fold(List(p)) {h =>
      if (h.id == p.id)
        h.copy(names = h.names ++ p.names) :: ps.drop(1)
        else
        p :: ps
    }
  }

  def fromDatabase(rows: Seq[(ProductId, LanguageCode, ProductName)]): Option[Product] = {
    val po = for {
      (id, c, n) <- rows.headOption
      t = Translation(lang = c, name = n)
      p <- Product(id = id, names = NonEmptySet.one(t)).some
    } yield p
    po.map(
      p =>
        rows.drop(1).foldLeft(p) { (a, cols) =>
          val (id, c, n) = cols
          a.copy(names = a.names.add(Translation(lang = c, name = n)))
        }
    )
  }
}
