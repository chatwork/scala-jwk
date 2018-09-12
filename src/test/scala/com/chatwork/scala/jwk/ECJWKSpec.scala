package com.chatwork.scala.jwk

import com.chatwork.scala.jwk.JWKError.JWKCreationError
import com.github.j5ik2o.base64scala.Base64String
import org.scalatest.{FreeSpec, Matchers}

class ECJWKSpec extends FreeSpec with Matchers with ECJWKJsonImplicits {
  object P_256 {
    val crv = Curve.P_256
    val x   = Base64String("MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4", urlSafe = true)
    val y   = Base64String("4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM", urlSafe = true)
    val d   = Base64String("870MB6gfuTJ4HtUnUvYMyJpr5eUZNP4Bk43bVdj3eAE", urlSafe = true)
  }
  object P_256_Alt {
    val crv = Curve.P_256
    val x   = Base64String("3l2Da_flYc-AuUTm2QzxgyvJxYM_2TeB9DMlwz7j1PE", urlSafe = true)
    val y   = Base64String("-kjT7Wrfhwsi9SG6H4UXiyUiVE9GHCLauslksZ3-_t0", urlSafe = true)
  }
  object P_384_Alt {
    val crv = Curve.P_384
    val x   = Base64String("Xy0mn0LmRyDBeHBjZrqH9z5Weu5pzCZYl1FJGHdoEj1utAoCpD4-Wn3VAIT-qgFF", urlSafe = true)
    val y   = Base64String("mrZQ1aB1E7JksXe6LXmM3BiGzqtlwCtMN0cpJb5EU62JMSISSK8l7cXSFt84A25z", urlSafe = true)
  }
  object P_521_Alt {
    val crv = Curve.P_521
    val x = Base64String("AfwEaSkqoPQynn4SdAXOqbyDuK6KsbI04i-6aWvh3GdvREZuHaWFyg791gcvJ4OqG13-gzfYxZxfblPMqfOtQrzk",
                         urlSafe = true)
    val y = Base64String("AHgOZhhJb2ZiozkquiEa0Z9SfERJbWaaE7qEnCuk9VVZaWruKWKNzZadoIRPt8h305r14KRoxu8AfV20X-d_2Ups",
                         urlSafe = true)
  }
  "ECJWK" - {
    "key size" in {
      ECJWK(P_256.crv, P_256.x, P_256.y, d = Some(P_256.d)).right.get.size.right.get shouldBe 256
      ECJWK(P_256_Alt.crv, P_256_Alt.x, P_256_Alt.y).right.get.size.right.get shouldBe 256
      ECJWK(P_384_Alt.crv, P_384_Alt.x, P_384_Alt.y).right.get.size.right.get shouldBe 384
      ECJWK(P_521_Alt.crv, P_521_Alt.x, P_521_Alt.y).right.get.size.right.get shouldBe 521
    }
    "supported Curves Constant" in {
      ECJWK.SUPPORTED_CURVES.contains(Curve.P_256) shouldBe true
      ECJWK.SUPPORTED_CURVES.contains(Curve.P_256K) shouldBe true
      ECJWK.SUPPORTED_CURVES.contains(Curve.P_384) shouldBe true
      ECJWK.SUPPORTED_CURVES.contains(Curve.P_521) shouldBe true
      ECJWK.SUPPORTED_CURVES.size shouldBe 4
    }
    "unknown Curve" in {
      val jwk = ECJWK(Curve("unknown", None, None), P_256.x, P_256.y)
      jwk match {
        case Left(e: JWKCreationError) =>
          println(e.message)
        case Left(e) =>
          fail()
        case Right(_) =>
          fail()
      }
    }
    "testJose4jVectorP256" in {
      val json = "{\"kty\":\"EC\"," +
      "\"x\":\"CEuRLUISufhcjrj-32N0Bvl3KPMiHH9iSw4ohN9jxrA\"," +
      "\"y\":\"EldWz_iXSK3l_S7n4w_t3baxos7o9yqX0IjzG959vHc\"," +
      "\"crv\":\"P-256\"}"
      val jwk = ECJWK.parseFromText(json).right.get
      jwk.keyType shouldBe KeyType.EC
      jwk.curve shouldBe Curve.P_256
      val result = jwk.computeThumbprint.right.get
      result.asString shouldBe "W6b8Mt2xhDFiy8sJe-MoWXIIkbty0HDhRjfI3VYWH6s"
    }
    "testJose4jVectorP384" in {
      val json = "{\"kty\":\"EC\"," +
      " \"x\":\"2jCG5DmKUql9YPn7F2C-0ljWEbj8O8-vn5Ih1k7Wzb-y3NpBLiG1BiRa392b1kcQ\"," +
      " \"y\":\"7Ragi9rT-5tSzaMbJlH_EIJl6rNFfj4V4RyFM5U2z4j1hesX5JXa8dWOsE-5wPIl\"," +
      " \"crv\":\"P-384\"}"
      val jwk = ECJWK.parseFromText(json).right.get
      jwk.keyType shouldBe KeyType.EC
      jwk.curve shouldBe Curve.P_384
      val result = jwk.computeThumbprint.right.get
      result.asString shouldBe "S-6tPnrLPensd2med1er_jX_j7mythdvKIj9O_sNqL0"
    }
    "testJose4jVectorP521" in {
      val json = "{\"kty\":\"EC\"," +
      "\"x\":\"Aeq3uMrb3iCQEt0PzSeZMmrmYhsKP5DM1oMP6LQzTFQY9-F3Ab45xiK4AJxltXEI-87g3gRwId88hTyHgq180JDt\"," +
      "\"y\":\"ARA0lIlrZMEzaXyXE4hjEkc50y_JON3qL7HSae9VuWpOv_2kit8p3pyJBiRb468_U5ztLT7FvDvtimyS42trhDTu\"," +
      "\"crv\":\"P-521\"}"
      val jwk = ECJWK.parseFromText(json).right.get
      jwk.keyType shouldBe KeyType.EC
      jwk.curve shouldBe Curve.P_521
      val result = jwk.computeThumbprint.right.get
      result.asString shouldBe "EroitZ-og3Ji6ENuMuey6vEz4hA2i56rOJHfrTeDHII"
    }
  }
}
