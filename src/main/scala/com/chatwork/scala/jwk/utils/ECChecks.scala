package com.chatwork.scala.jwk.utils

import java.security.interfaces.{ECPrivateKey, ECPublicKey}
import java.security.spec.{ECFieldFp, ECParameterSpec}

object ECChecks {

  def isPointOnCurve(publicKey: ECPublicKey, privateKey: ECPrivateKey): Boolean =
    isPointOnCurve(publicKey, privateKey.getParams)

  def isPointOnCurve(publicKey: ECPublicKey, ecParameterSpec: ECParameterSpec): Boolean = {
    val point = publicKey.getW
    isPointOnCurve(BigInt(point.getAffineX), BigInt(point.getAffineY), ecParameterSpec)
  }

  def isPointOnCurve(x: BigInt, y: BigInt, ecParameterSpec: ECParameterSpec): Boolean = { // Ensure the following condition is met:
    // (y^2) mod p = (x^3 + ax + b) mod p
    val curve     = ecParameterSpec.getCurve
    val a         = curve.getA
    val b         = curve.getB
    val p         = curve.getField.asInstanceOf[ECFieldFp].getP
    val leftSide  = y.bigInteger.pow(2).mod(p)
    val rightSide = x.bigInteger.pow(3).add(a.multiply(x.bigInteger)).add(b).mod(p)
    leftSide == rightSide
  }

}
