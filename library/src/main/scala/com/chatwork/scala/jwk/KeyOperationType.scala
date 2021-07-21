package com.chatwork.scala.jwk

import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class KeyOperationType(val entryName: String) extends Product with Serializable

object KeyOperationType {
  val values: immutable.IndexedSeq[KeyOperationType] =
    immutable.IndexedSeq(Sign, Verify, Encrypt, Decrypt, WrapKey, UnwrapKey, DeriveKey, DeriveBits)

  def withNameEither(name: String): Either[String, KeyOperationType] =
    values.find(_.entryName == name).toRight(s"Unknown key operation type $name")

  case object Sign       extends KeyOperationType("sign")
  case object Verify     extends KeyOperationType("verify")
  case object Encrypt    extends KeyOperationType("encrypt")
  case object Decrypt    extends KeyOperationType("decrypt")
  case object WrapKey    extends KeyOperationType("wrapKey")
  case object UnwrapKey  extends KeyOperationType("unwrapKey")
  case object DeriveKey  extends KeyOperationType("deriveKey")
  case object DeriveBits extends KeyOperationType("deriveBits")
}

trait KeyOperationTypeJsonImplicits {

  implicit val publicKeyUseJsonEncoder: Encoder[KeyOperationType] = Encoder[String].contramap(_.entryName)

  implicit val publicKeyUseJsonDecoder: Decoder[KeyOperationType] =
    Decoder[String].emap(KeyOperationType.withNameEither)

}
