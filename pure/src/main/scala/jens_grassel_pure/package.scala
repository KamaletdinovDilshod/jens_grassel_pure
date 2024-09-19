import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._

package object jens_grassel_pure {
  // A string containing a database login which must be non empty.
  type DatabaseLogin    = String Refined NonEmpty
  // A string containing a database password which must be non empty.
  type DatabasePassword = String Refined NonEmpty
  // A string containing a database url.
  type DatabaseUrl      = String Refined Uri
  // A string that must not be empty.
  type NonEmptyString   = String Refined NonEmpty
  // A TCP port number which is valid in the range of 1 to 65535.
  type PortNumber       = Int Refined Interval.Closed[W.`1`.T, W.`65535`.T]
}
