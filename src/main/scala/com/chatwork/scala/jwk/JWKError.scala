package com.chatwork.scala.jwk

import java.util.concurrent.atomic.AtomicInteger

trait JWKError

object JWKError {
  final val BASE_ERROR_CODE = 1000
  val generator             = new AtomicInteger(BASE_ERROR_CODE)

  case class JOSEError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class JWKCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class JWKSetCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class RSAPrivateKeyCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class RSAPublicKeyCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class JWKThumbprintError(message: String, cause: Option[JWKError] = None) extends JWKError

}
