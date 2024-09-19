package jens_grassel_pure

import jens_grassel_pure.models.TypeGenerators
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

abstract class BaseSpec extends AnyWordSpec with Matchers with ScalaCheckDrivenPropertyChecks with TypeGenerators {}
