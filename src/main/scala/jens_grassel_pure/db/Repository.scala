package jens_grassel_pure.db

import fs2.Stream
import jens_grassel_pure.models._

trait Repository[F[_]] {
  def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]]

  def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)]

  def saveProduct(p: Product): F[Int]

  def updateProduct(p: Product): F[Int]

}
