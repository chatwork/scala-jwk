package com.chatwork.scala.jwk

import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.UUID

import com.github.j5ik2o.base64scala.Base64String
import org.scalatest.FreeSpec

class KeyIdSpec extends FreeSpec {

  private val n = "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx" +
    "4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMs" +
    "tn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2" +
    "QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbI" +
    "SD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqb" +
    "w0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw"

  private val e = "AQAB"

  "KeyId" - {
    "should be able to construct" in {
      val keyIdValue = UUID.randomUUID().toString
      val keyId      = new KeyId(keyIdValue)
      assert(keyId.value === keyIdValue)
    }
    "should be able to create from RSA PublicKey params" in {
      val result =
        KeyId.fromRSAPublicKeyParams(Base64String(n, urlSafe = true), Base64String(e, urlSafe = true))
      println(result)
      assert(result === Right(KeyId("zxHHCn6NUeqNxYOKWGV8kyUkgPOuwpIIF8_Fj0aaIIo")))
    }
    "should be able to create from RSA PublicKey" in {
      val keyId = for {
        modulus        <- Base64String(n, urlSafe = true).decodeToBigInt.map(_.bigInteger)
        publicExponent <- Base64String(e, urlSafe = true).decodeToBigInt.map(_.bigInteger)
        keySpec        <- Right(new RSAPublicKeySpec(modulus, publicExponent))
        result <- {
          val factory = KeyFactory.getInstance("RSA")
          KeyId.fromRSAPublicKey(factory.generatePublic(keySpec).asInstanceOf[RSAPublicKey])
        }
      } yield result
      assert(keyId === Right(KeyId("zxHHCn6NUeqNxYOKWGV8kyUkgPOuwpIIF8_Fj0aaIIo")))
    }
  }
}
