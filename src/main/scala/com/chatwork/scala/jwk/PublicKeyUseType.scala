package com.chatwork.scala.jwk

import enumeratum._
import io.circe.{Decoder, Encoder}

import scala.collection.immutable

sealed abstract class PublicKeyUseType(override val entryName: String) extends EnumEntry

object PublicKeyUseType extends Enum[PublicKeyUseType] {

  override val values: immutable.IndexedSeq[PublicKeyUseType] = findValues

  case object Signature  extends PublicKeyUseType("sig")
  case object Encryption extends PublicKeyUseType("enc")
}

trait PublicKeyUseJsonImplicits {

  implicit val PublicKeyUseJsonEncoder: Encoder[PublicKeyUseType] = Encoder[String].contramap(_.entryName)

  implicit val PublicKeyUseJsonDecoder: Decoder[PublicKeyUseType] = Decoder[String].map(PublicKeyUseType.withName)

}
