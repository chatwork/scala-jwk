package com.chatwork.scala.jwk

import java.security.{KeyPair, PrivateKey, PublicKey}

import com.chatwork.scala.jwk.JWKError.{RSAKeyCreationError, RSAPrivateKeyCreationError, RSAPublicKeyCreationError}

trait AssymetricJWK {

  def toPublicKey: Either[RSAPublicKeyCreationError, PublicKey]

  def toPrivateKey: Either[RSAPrivateKeyCreationError, PrivateKey]

  def toKeyPair: Either[RSAKeyCreationError, KeyPair]

}
