package com.chatwork.scala.jwk

import java.nio.charset.Charset
import java.security.{MessageDigest, NoSuchAlgorithmException}

import io.circe.Json
import io.circe.syntax._
import cats.implicits._
import com.chatwork.scala.jwk.JWKError.JWKThumbprintError
import com.github.j5ik2o.base64scala.{Base64String, Base64StringFactory}

object JWKThumbprint extends JWKJsonImplicits {

  def computeFromJWK(jwk: JWK,
                     hashAlg: String = "SHA-256"): Either[JWKThumbprintError, Base64String] =
    computeFromJson(jwk.asJson, hashAlg)

  private def getDigest(json: Json, hashAlg: String): Either[JWKThumbprintError, Array[Byte]] = {
    try {
      val md = MessageDigest.getInstance(hashAlg)
      md.update(json.noSpaces.getBytes(Charset.defaultCharset()))
      Right(md.digest())
    } catch {
      case ex: NoSuchAlgorithmException =>
        Left(JWKThumbprintError("Couldn't compute JWK thumbprint: Unsupported hash algorithm: " + ex.getMessage))
    }
  }

  def computeFromJson(
      json: Json,
      hashAlg: String = "SHA-256"
  ): Either[JWKThumbprintError, Base64String] = {
    for {
      v <- getDigest(json, hashAlg)
      r <- Base64StringFactory(urlSafe = true, isNoPadding = true).encode(v).leftMap(error => JWKThumbprintError(error.message))
    } yield r
  }

}
