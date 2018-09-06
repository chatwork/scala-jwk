package com.chatwork.scala.jwk

import com.chatwork.scala.jwk.JWKError.JOSEError

object ByteUtils {

  def safeBitLength(byteArray: Array[Byte]): Either[JOSEError, Int] =
    safeBitLength(byteArray.length)

  def safeBitLength(byteLength: Int): Either[JOSEError, Int] = {
    val longResult = byteLength.toLong * 8.toLong
    if (longResult.toInt.toLong != longResult) Left(JOSEError("Overflow error"))
    else Right(longResult.toInt)
  }

}
