package com.chatwork.scala.jwk

import enumeratum._
import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class KeyOperationType(override val entryName: String) extends EnumEntry

object KeyOperationType extends Enum[KeyOperationType] {
  override val values: immutable.IndexedSeq[KeyOperationType] = findValues
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

  implicit val publicKeyUseJsonDecoder: Decoder[KeyOperationType] = Decoder[String].map(KeyOperationType.withName)

}
