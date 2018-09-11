package com.chatwork.scala.jwk

import java.security.spec.ECParameterSpec

case class Curve(name: String, stdName: Option[String] = None, oid: Option[String] = None) {

  def toECParameterSpec: Option[ECParameterSpec] = ECParameterTable.get(this)
}

object Curve {

  val P_256 = Curve("P-256", Some("secp256r1"), Some("1.2.840.10045.3.1.7"))

  val P_256K = Curve("P-256K", Some("secp256k1"), Some("1.3.132.0.10"))

  val P_384 = Curve("P-384", Some("secp384r1"), Some("1.3.132.0.34"))

  val P_521 = Curve("P-521", Some("secp521r1"), Some("1.3.132.0.35"))

  val Ed25519 = Curve("Ed25519", Some("Ed25519"), None)

  val Ed448 = Curve("Ed448", Some("Ed448"), None)

  val X25519 = Curve("X25519", Some("X25519"), None)

  val X448 = Curve("X448", Some("X448"), None)

  private val values = Set(P_256, P_256K, P_384, P_521, Ed25519, Ed448, X25519, X448)

  def withName(name: String): Option[Curve] = {
    values.find(_.name == name)
  }

  def withStdName(stdName: String): Option[Curve] =
    if ("secp256r1" == stdName || "prime256v1" == stdName) Some(P_256)
    else if ("secp256k1" == stdName) Some(P_256K)
    else if ("secp384r1" == stdName) Some(P_384)
    else if ("secp521r1" == stdName) Some(P_521)
    else if (Ed25519.stdName.contains(stdName)) Some(Ed25519)
    else if (Ed448.stdName.contains(stdName)) Some(Ed448)
    else if (X25519.stdName.contains(stdName)) Some(X25519)
    else if (X448.stdName.contains(stdName)) Some(X448)
    else None

  def withOID(oid: String): Option[Curve] =
    if (P_256.oid.contains(oid))
      Some(P_256)
    else if (P_256K.oid.contains(oid))
      Some(P_256K)
    else if (P_384.oid.contains(oid))
      Some(P_384)
    else if (P_521.oid.contains(oid))
      Some(P_521)
    else
      None

}
