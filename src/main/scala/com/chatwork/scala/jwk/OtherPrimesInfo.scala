package com.chatwork.scala.jwk

import com.github.j5ik2o.base64scala.Base64String

case class OtherPrimesInfo(
    r: Base64String,
    d: Base64String,
    t: Base64String
) {
  val primeFactor          = r
  val factorCRTExponent    = d
  val factorCRTCoefficient = t
}
