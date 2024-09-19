package jens_grassel_pure.db

import fs2.Stream
import jens_grassel_pure.models._

/**
 * A base class for our database repository.
 *
 * @tparam F A higher kinded type which wraps the actual return value.
 */


trait Repository[F[_]] {
  /**
   * Load a product from the database repository.
   *
   * @param id The unique ID of the product.
   * @return A list of database rows for a single product which you'll need to combine.
   */

  def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]]

  /**
   * Load a product from the database repository.
   *
   * @param id The unique ID of the product.
   * @return A list of database rows for a single product which you'll need to combine.
   */

  def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)]

  /**
   * Save the given product in the database.
   *
   * @param p A product to be saved.
   * @return The number of affected database rows (product + translations).
   */
  def saveProduct(p: Product): F[Int]

  /**
   * Update the given product in the database.
   *
   * @param p The product to be updated.
   * @return The number of affected database rows.
   */

  def updateProduct(p: Product): F[Int]

}
