package com.chatwork.scala.jwk

import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class PublicKeyUseType(val entryName: String) extends Product with Serializable

object PublicKeyUseType {

  val values: immutable.IndexedSeq[PublicKeyUseType] = immutable.IndexedSeq(Signature, Encryption)

  def withNameEither(name: String): Either[String, PublicKeyUseType] =
    values.find(_.entryName == name).toRight(s"Unknown public key use type $name")

  case object Signature  extends PublicKeyUseType("sig")
  case object Encryption extends PublicKeyUseType("enc")
}

trait PublicKeyUseJsonImplicits {

  implicit val PublicKeyUseJsonEncoder: Encoder[PublicKeyUseType] = Encoder[String].contramap(_.entryName)

  implicit val PublicKeyUseJsonDecoder: Decoder[PublicKeyUseType] =
    Decoder[String].emap(PublicKeyUseType.withNameEither)

}
