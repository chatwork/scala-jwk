package com.chatwork.scala.jwk

import java.security.{ KeyPair, PrivateKey, PublicKey }

trait AssymetricJWK {

  def toPublicKey: Either[JWKError, PublicKey]

  def toPrivateKey: Either[JWKError, PrivateKey]

  def toKeyPair: Either[JWKError, KeyPair]

}
