package jens_grassel_pure

import cats._
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection._
import eu.timepit.refined.string._

import java.util.UUID

package object models {
  // A language code format according to ISO 639-1. Please note that this only verifies the format!
  type LanguageCode = String Refined MatchesRegex[W.`"^[a-z]{2}$"`.T]
  object LanguageCode extends RefinedTypeOps[LanguageCode, String] with CatsRefinedTypeOpsSyntax
  // A product id which must be a valid UUID in version 4.
  type ProductId   = UUID
  // A product name must be a non-empty string.
  type ProductName = String Refined NonEmpty
  object ProductName extends RefinedTypeOps[ProductName, String] with CatsRefinedTypeOpsSyntax

  implicit val orderLanguageCode: Order[LanguageCode] = new Order[LanguageCode] {
    def compare(x: LanguageCode, y: LanguageCode): Int = x.value.compare(y.value)
  }

  implicit val orderProductId: Order[ProductId] = new Order[ProductId] {
    def compare(x: ProductId, y: ProductId): Int = x.toString.compare(y.toString)
  }

  implicit val orderProductName: Order[ProductName] = new Order[ProductName] {
    def compare(x: ProductName, y: ProductName): Int = x.value.compare(y.value)
  }
}
