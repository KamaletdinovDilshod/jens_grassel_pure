package jens_grassel_pure.models

import cats.data.{NonEmptyList, NonEmptySet}
import eu.timepit.refined.api.{RefType, Refined}
import org.scalacheck.{Arbitrary, Gen}

import java.util.UUID

trait TypeGenerators2 {
  val DefaultProductName: ProductName = Refined.unsafeApply("I am a product")

  val genLanguageCode: Gen[LanguageCode] = Gen.oneOf(LanguageCodes.all)

  val genUuid: Gen[UUID] = Gen.delay(UUID.randomUUID())

  val genProductId: Gen[ProductId] = genUuid

  implicit val arbitraryProductId: Arbitrary[ProductId] = Arbitrary(genProductId)

  val genProductName: Gen[ProductName] = for {
    cs  <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    name = RefType.applyRef[ProductName](cs.mkString).getOrElse(DefaultProductName)
  } yield name

  val genTranslation: Gen[Translation] = for {
    c <- genLanguageCode
    n <- genProductName
  } yield Translation(
    lang = c,
    name = n
  )

  implicit val arbitraryTranslation: Arbitrary[Translation] = Arbitrary(genTranslation)

  val genTranslationList: Gen[List[Translation]] = for {
    ts <- Gen.nonEmptyListOf(genTranslation)
  } yield ts

  val genNonEmptyTranslationSet: Gen[NonEmptySet[Translation]] = for {
    t  <- genTranslation
    ts <- genTranslationList
    ns  = NonEmptyList.fromList(ts).map(_.toNes)
  } yield ns.getOrElse(NonEmptySet.one(t))

  val genProduct: Gen[Product] = for {
    id <- genProductId
    ts <- genNonEmptyTranslationSet
  } yield Product(
    id = id,
    names = ts
  )

  implicit val arbitraryProduct: Arbitrary[Product] = Arbitrary(genProduct)

  val genProducts: Gen[List[Product]] = Gen.nonEmptyListOf(genProduct)

  implicit val arbitraryProducts: Arbitrary[List[Product]] = Arbitrary(genProducts)
}
