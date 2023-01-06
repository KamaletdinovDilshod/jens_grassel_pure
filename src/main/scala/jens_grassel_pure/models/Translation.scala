package jens_grassel_pure.models

import cats._
import cats.derived
import io.circe._
import io.circe.refined._ // DON'T REMOVE IT
import io.circe.generic.semiauto._

final case class Translation(lang: LanguageCode, name: ProductName)

object Translation {
  implicit val decode: Decoder[Translation] = deriveDecoder[Translation]
  implicit val encode: Encoder[Translation] = deriveEncoder[Translation]


  implicit val order: Order[Translation] = Order.fromLessThan[Translation]((_, _) => true)
}
