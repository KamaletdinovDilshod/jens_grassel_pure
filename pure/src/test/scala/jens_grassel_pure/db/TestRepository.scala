package jens_grassel_pure.db

import cats.effect.kernel.Async
import cats.implicits._
import fs2.Stream
import jens_grassel_pure.models._

import scala.collection.immutable._

class TestRepository[F[_]: Async](data: Seq[Product]) extends Repository[F] {
  override def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]] =
    data.find(_.id === id) match {
      case None    => Seq.empty.pure[F]
      case Some(p) =>
        val ns = p.names.toNonEmptyList.toList.iterator.toSeq
        ns.map(n => (p.id, n.lang, n.name)).pure[F]
    }

  override def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)] = {
    val rows = data.toList.flatMap { p =>
      val ns = p.names.toNonEmptyList.toList.iterator.toSeq
      ns.map(n => (p.id, n.lang, n.name))
    }
    Stream.emits(rows)
  }

  override def saveProduct(p: Product): F[Int] = {
    data.find(_.id === p.id).fold(0.pure[F])(_ => 1.pure[F])
  }

  override def updateProduct(p: Product): F[Int] =
    data.find(_.id === p.id).fold(0.pure[F])(_ => 1.pure[F])
}
