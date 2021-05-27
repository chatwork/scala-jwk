package com.chatwork.scala.jwk

import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class KeyType(val entryName: String) extends Product with Serializable

object KeyType {

  val values: immutable.IndexedSeq[KeyType] = immutable.IndexedSeq(RSA, EC)

  def withNameEither(name: String): Either[String, KeyType] =
    values.find(_.entryName == name).toRight(s"Unknown key type $name")

  case object RSA extends KeyType("RSA")

  case object EC extends KeyType("EC")

  case class Other(name: String) extends KeyType(name)

}

trait KeyTypeJsonImplicits {

  implicit val KeyTypeJsonEncoder: Encoder[KeyType] = Encoder[String].contramap(_.entryName)

  implicit val KeyTypeJsonDecoder: Decoder[KeyType] =
    Decoder[String].map(name => KeyType.withNameEither(name).getOrElse(KeyType.Other(name)))

}
