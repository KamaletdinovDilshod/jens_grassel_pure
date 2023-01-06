package jens_grassel_pure

import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

abstract class BaseSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks {}
