package com.chatwork.scala.jwk

import java.security.interfaces.RSAPublicKey

import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import cats.implicits._
import com.chatwork.scala.jwk.JWKError.JWKThumbprintError
import com.github.j5ik2o.base64scala.{Base64String, Base64StringFactory}

case class KeyId(value: String)

object KeyId extends KeyIdJsonImplicits with Base64StringJsonImplicits {

  val base64StringFactory = Base64StringFactory(urlSafe = true, isNoPadding = true)

  def fromRSAPublicKeyParams(modulus: Base64String, publicExponent: Base64String): Either[JWKThumbprintError, KeyId] = {
    require(modulus.urlSafe)
    val json =
      Json.obj("kty" -> Json.fromString(KeyType.RSA.toString), "n" -> modulus.asJson, "e" -> publicExponent.asJson)
    JWKThumbprint.computeFromJson(json).map(v => new KeyId(v.asString))
  }

  def fromRSAPublicKey(publicKey: RSAPublicKey): Either[JWKThumbprintError, KeyId] = {
    for {
      n <- base64StringFactory
        .encode(publicKey.getModulus)
        .leftMap(error => JWKThumbprintError(error.message))
      e <- base64StringFactory
        .encode(publicKey.getPublicExponent)
        .leftMap(error => JWKThumbprintError(error.message))
      result <- fromRSAPublicKeyParams(n, e)
    } yield result
  }

}

trait KeyIdJsonImplicits {

  implicit val KeyIdJsonEncoder: Encoder[KeyId] = Encoder[String].contramap(_.value)

  implicit val KeyJsonDecoder: Decoder[KeyId] = Decoder[String].map(new KeyId(_))

}
