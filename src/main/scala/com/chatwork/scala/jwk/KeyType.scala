package com.chatwork.scala.jwk

import enumeratum._
import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class KeyType(override val entryName: String) extends EnumEntry

object KeyType extends Enum[KeyType] {

  override val values: immutable.IndexedSeq[KeyType] = findValues

  case object RSA extends KeyType("RSA")

  case object EC extends KeyType("EC")

  case class Other(name: String) extends KeyType(name)

}

trait KeyTypeJsonImplicits {

  implicit val KeyTypeJsonEncoder: Encoder[KeyType] = Encoder[String].contramap(_.entryName)

  implicit val KeyTypeJsonDecoder: Decoder[KeyType] = Decoder[String].map(KeyType.withName)

}
