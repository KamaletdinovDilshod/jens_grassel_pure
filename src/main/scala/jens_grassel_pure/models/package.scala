package jens_grassel_pure

import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string._

package object models {
  // A language code format according to ISO 639-1. Please note that this only verifies the format!
  type LanguageCode = String Refined MatchesRegex[W.`"^[a-z]{2}$"`.T]

  object LanguageCode extends RefinedTypeOps[LanguageCode, String] with CatsRefinedTypeOpsSyntax
  // A String containing a database login which must be non empty.
  type DatabaseLogin = String Refined NonEmpty
  // A String containing a database password which must be non empty.
  type DatabasePassword = String Refined NonEmpty
  // A String containing a database url.
  type DatabaseUrl = String Refined Uri
  // A String that must not be empty
  type NonEmptyString = String Refined NonEmpty
  // A TCP port number which is valid in the range of 1 to 65535.
  type PortNumber = Int Refined Interval.Closed[W.`1`.T, W.`65535`.T]
  type ProductId = java.util.UUID
  type ProductName = String Refined NonEmpty
}
