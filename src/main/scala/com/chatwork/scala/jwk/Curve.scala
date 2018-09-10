package com.chatwork.scala.jwk

import java.security.spec.ECParameterSpec

case class Curve(name: String, stdName: Option[String], oid: Option[String]) {

  def toECParameterSpec: Option[ECParameterSpec] = ECParameterTable.get(this)
}

object Curve {

  val P_256 = Curve("P-256", Some("secp256r1"), Some("1.2.840.10045.3.1.7"))

  val P_384 = Curve("P-384", Some("secp384r1"), Some("1.3.132.0.34"))

  val P_521 = Curve("P-521", Some("secp521r1"), Some("1.3.132.0.35"))

  val Ed25519 = Curve("Ed25519", Some("Ed25519"), None)

  val Ed448 = Curve("Ed448", Some("Ed448"), None)

  val X25519 = Curve("X25519", Some("X25519"), None)

  val X448 = Curve("X448", Some("X448"), None)

  private val values = Set(P_256, P_384, P_521, Ed25519, Ed448, X25519, X448)

  def withName(name: String): Option[Curve] = {
    values.find(_.name == name)
  }

}
