package com.chatwork.scala.jwk

import java.util.concurrent.atomic.AtomicInteger

trait JWKError

object JWKError {
  final val BASE_ERROR_CODE = 1000
  val generator             = new AtomicInteger(BASE_ERROR_CODE)

  case class JOSEError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class JWKCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class JWKSetCreationError(message: String, cause: Option[JWKError] = None) extends JWKError

  sealed trait KeyCreationError extends JWKError

  case class PrivateKeyCreationError(message: String, cause: Option[JWKError] = None) extends KeyCreationError

  case class PublicKeyCreationError(message: String, cause: Option[JWKError] = None) extends KeyCreationError

  case class JWKThumbprintError(message: String, cause: Option[JWKError] = None) extends JWKError

  case class Cause(throwable: Throwable) extends JWKError

}
