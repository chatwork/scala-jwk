package com.chatwork.scala.jwk

import java.security.{KeyPair, PrivateKey, PublicKey}

import com.chatwork.scala.jwk.JWKError.{KeyCreationError, PrivateKeyCreationError, PublicKeyCreationError}

trait AssymetricJWK {

  def toPublicKey: Either[PublicKeyCreationError, PublicKey]

  def toPrivateKey: Either[PrivateKeyCreationError, PrivateKey]

  def toKeyPair: Either[KeyCreationError, KeyPair]

}
