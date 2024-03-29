package com.chatwork.scala.jwk

import cats.data.NonEmptyList

import java.net.URI
import java.security.interfaces.{ RSAPrivateCrtKey, RSAPrivateKey, RSAPublicKey }
import java.security.spec.RSAPrivateKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import com.github.j5ik2o.base64scala.{ Base64String, Base64StringFactory }
import io.circe.parser._
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AnyFreeSpec

class RSAJWKSpec extends AnyFreeSpec with Matchers with OptionValues with RSAJWKJsonImplicits {
  val base64StringFactory = Base64StringFactory(urlSafe = true, isNoPadding = true)
  private val n = "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx" +
    "4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMs" +
    "tn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2" +
    "QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbI" +
    "SD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqb" +
    "w0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw"

  private val e = "AQAB"

  private val d = "X4cTteJY_gn4FYPsXB8rdXix5vwsg1FLN5E3EaG6RJoVH-HLLKD9" +
    "M7dx5oo7GURknchnrRweUkC7hT5fJLM0WbFAKNLWY2vv7B6NqXSzUvxT0_YSfqij" +
    "wp3RTzlBaCxWp4doFk5N2o8Gy_nHNKroADIkJ46pRUohsXywbReAdYaMwFs9tv8d" +
    "_cPVY3i07a3t8MN6TNwm0dSawm9v47UiCl3Sk5ZiG7xojPLu4sbg1U2jx4IBTNBz" +
    "nbJSzFHK66jT8bgkuqsk0GjskDJk19Z4qwjwbsnn4j2WBii3RL-Us2lGVkY8fkFz" +
    "me1z0HbIkfz0Y6mqnOYtqc0X4jfcKoAC8Q"

  private val p = "83i-7IvMGXoMXCskv73TKr8637FiO7Z27zv8oj6pbWUQyLPQBQxtPV" +
    "nwD20R-60eTDmD2ujnMt5PoqMrm8RfmNhVWDtjjMmCMjOpSXicFHj7XOuVIYQyqV" +
    "WlWEh6dN36GVZYk93N8Bc9vY41xy8B9RzzOGVQzXvNEvn7O0nVbfs"

  private val q = "3dfOR9cuYq-0S-mkFLzgItgMEfFzB2q3hWehMuG0oCuqnb3vobLyum" +
    "qjVZQO1dIrdwgTnCdpYzBcOfW5r370AFXjiWft_NGEiovonizhKpo9VVS78TzFgx" +
    "kIdrecRezsZ-1kYd_s1qDbxtkDEgfAITAG9LUnADun4vIcb6yelxk"

  private val dp = "G4sPXkc6Ya9y8oJW9_ILj4xuppu0lzi_H7VTkS8xj5SdX3coE0oim" +
    "YwxIi2emTAue0UOa5dpgFGyBJ4c8tQ2VF402XRugKDTP8akYhFo5tAA77Qe_Nmtu" +
    "YZc3C3m3I24G2GvR5sSDxUyAN2zq8Lfn9EUms6rY3Ob8YeiKkTiBj0"

  private val dq = "s9lAH9fggBsoFR8Oac2R_E2gw282rT2kGOAhvIllETE1efrA6huUU" +
    "vMfBcMpn8lqeW6vzznYY5SSQF7pMdC_agI3nG8Ibp1BUb0JUiraRNqUfLhcQb_d9" +
    "GF4Dh7e74WbRsobRonujTYN1xCaP6TO61jvWrX-L18txXw494Q_cgk"

  private val qi = "GyM_p6JrXySiz1toFgKbWV-JdI3jQ4ypu9rbMWx3rQJBfmt0FoYzg" +
    "UIZEVFEcOqwemRN81zoDAaa-Bk0KWNGDjJHZDdDmFhW3AN7lI-puxk_mHZGJ11rx" +
    "yR8O55XLSe3SPmRfKwZI6yU24ZxvQKFYItdldUKGzO6Ia6zTKhAVRU"

  "RSAJWK" - {
    "should be able to full construct & serialization" in {
      val x5u    = new URI("http://example.com/jwk.json")
      val x5t    = Base64String("abc", urlSafe = true)
      val x5t256 = Base64String("abc256", urlSafe = true)
      val x5c    = NonEmptyList.one(Base64String("def", urlSafe = true))

      val factory = KeyFactory.getInstance("RSA")
      val privateKey = for {
        _n <- Base64String(n, urlSafe = true).decodeToBigInt.map(_.bigInteger)
        _d <- Base64String(d, urlSafe = true).decodeToBigInt.map(_.bigInteger)
      } yield {
        factory.generatePrivate(
          new RSAPrivateKeySpec(_n, _d)
        )
      }

      val key = new RSAJWK(
        n = Base64String(n, urlSafe = true),
        e = Base64String(e, urlSafe = true),
        publicKeyUseType = Some(PublicKeyUseType.Signature),
        keyOperations = KeyOperations.empty,
        algorithmType = Some(JWSAlgorithmType.RS256),
        keyId = Some(KeyId("1")),
        x509Url = Some(x5u),
        x509CertificateSHA1Thumbprint = Some(x5t),
        x509CertificateSHA256Thumbprint = Some(x5t256),
        x509CertificateChain = Some(x5c),
        d = Some(Base64String(d, urlSafe = true)),
        p = Some(Base64String(p, urlSafe = true)),
        q = Some(Base64String(q, urlSafe = true)),
        dp = Some(Base64String(dp, urlSafe = true)),
        dq = Some(Base64String(dq, urlSafe = true)),
        qi = Some(Base64String(qi, urlSafe = true)),
        privateKey = Some(privateKey.fold(_ => null, identity))
      )

      key.publicKeyUseType shouldBe Some(PublicKeyUseType.Signature)
      key.keyOperations shouldBe KeyOperations.empty
      key.algorithmType shouldBe Some(JWSAlgorithmType.RS256)
      key.keyId shouldBe Some(KeyId("1"))
      key.x509Url shouldBe Some(x5u)
      key.x509CertificateSHA1Thumbprint shouldBe Some(x5t)
      key.x509CertificateSHA256Thumbprint shouldBe Some(x5t256)
      key.x509CertificateChain shouldBe Some(x5c)
      key.x509CertificateChain.value.length shouldBe x5c.length

      key.modulus shouldBe Base64String(n, urlSafe = true)
      key.publicExponent shouldBe Base64String(e, urlSafe = true)

      key.privateExponent shouldBe Some(Base64String(d, urlSafe = true))

      key.firstPrimeFactor shouldBe Some(Base64String(p, urlSafe = true))
      key.secondPrimeFactor shouldBe Some(Base64String(q, urlSafe = true))

      key.firstFactorCRTExponent shouldBe Some(Base64String(dp, urlSafe = true))
      key.secondFactorCRTExponent shouldBe Some(Base64String(dq, urlSafe = true))

      key.firstCRTCoefficient shouldBe Some(Base64String(qi, urlSafe = true))

      key.isPrivate shouldBe true

      val publicKey = key.toPublicJWK
      publicKey.publicKeyUseType shouldBe Some(PublicKeyUseType.Signature)
      publicKey.keyOperations shouldBe KeyOperations.empty
      publicKey.algorithmType shouldBe Some(JWSAlgorithmType.RS256)
      publicKey.keyId shouldBe Some(KeyId("1"))
      publicKey.x509Url shouldBe Some(x5u)
      publicKey.x509CertificateSHA1Thumbprint shouldBe Some(x5t)
      publicKey.x509CertificateSHA256Thumbprint shouldBe Some(x5t256)
      publicKey.x509CertificateChain shouldBe Some(x5c)
      publicKey.x509CertificateChain.value.length shouldBe x5c.length

      publicKey.modulus shouldBe Base64String(n, urlSafe = true)
      publicKey.publicExponent shouldBe Base64String(e, urlSafe = true)

      publicKey.privateExponent shouldBe None

      publicKey.firstPrimeFactor shouldBe None
      publicKey.secondPrimeFactor shouldBe None

      publicKey.firstFactorCRTExponent shouldBe None
      publicKey.secondFactorCRTExponent shouldBe None

      publicKey.firstCRTCoefficient shouldBe None

    }
    "should be able to export & import RSAPublicKey" in {
      val key = new RSAJWK(
        n = Base64String(n, urlSafe = true),
        e = Base64String(e, urlSafe = true)
      )
      val pubKey = key.toRSAPublicKey
      pubKey.map(_.getModulus) shouldBe Base64String(n, urlSafe = true).decodeToBigInt.map(_.bigInteger)
      pubKey.map(_.getPublicExponent) shouldBe Base64String(e, urlSafe = true).decodeToBigInt.map(_.bigInteger)
      pubKey.map(_.getAlgorithm) shouldBe Right("RSA")

      val key2 = pubKey.flatMap { key => RSAJWK.fromRSAPublicKey(key) }
      key2.map(_.modulus) shouldBe Right(Base64String(n, urlSafe = true))
      key2.map(_.publicExponent) shouldBe Right(Base64String(e, urlSafe = true))

    }
    "should be able to export & import RSAPrivateKey" in {
      val key = new RSAJWK(
        n = Base64String(n, urlSafe = true),
        e = Base64String(e, urlSafe = true),
        publicKeyUseType = Some(PublicKeyUseType.Signature),
        keyOperations = KeyOperations.empty,
        algorithmType = Some(JWSAlgorithmType.RS256),
        keyId = Some(KeyId("1")),
        x509Url = None,
        x509CertificateSHA1Thumbprint = None,
        x509CertificateSHA256Thumbprint = None,
        x509CertificateChain = None,
        d = Some(Base64String(d, urlSafe = true)),
        p = Some(Base64String(p, urlSafe = true)),
        q = Some(Base64String(q, urlSafe = true)),
        dp = Some(Base64String(dp, urlSafe = true)),
        dq = Some(Base64String(dq, urlSafe = true)),
        qi = Some(Base64String(qi, urlSafe = true))
      )

      // Private key export with CRT (2nd form)
      val privKey1 = key.toRSAPrivateKey
      privKey1.map(_.map(_.getModulus)) shouldBe Base64String(n, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privKey1.map(_.map(_.getPrivateExponent)) shouldBe Base64String(d, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privKey1.exists(_.fold(false)(_.isInstanceOf[RSAPrivateCrtKey]))
      val privCrtKey = privKey1.map(_.map(_.asInstanceOf[RSAPrivateCrtKey]))
      privCrtKey.map(_.map(_.getPublicExponent)) shouldBe Base64String(e, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privCrtKey.map(_.map(_.getPrimeP)) shouldBe Base64String(p, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privCrtKey.map(_.map(_.getPrimeQ)) shouldBe Base64String(q, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privCrtKey.map(_.map(_.getPrimeExponentP)) shouldBe Base64String(dp, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privCrtKey.map(_.map(_.getPrimeExponentQ)) shouldBe Base64String(dq, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      privCrtKey.map(_.map(_.getCrtCoefficient)) shouldBe Base64String(qi, urlSafe = true).decodeToBigInt
        .map(e => Some(e.bigInteger))
      // Key pair export
      val keyPair = key.toKeyPair
      val pubKey  = keyPair.map(_.getPublic.asInstanceOf[RSAPublicKey])
      pubKey.map(_.getModulus) shouldBe Base64String(n, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      pubKey.map(_.getPublicExponent) shouldBe Base64String(e, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      val privKey2 = keyPair.map(_.getPrivate.asInstanceOf[RSAPrivateKey])
      privKey2.map(_.getModulus) shouldBe Base64String(n, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privKey2.map(_.getPrivateExponent) shouldBe Base64String(d, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      val privCrtKey2 = privKey2.map(_.asInstanceOf[RSAPrivateCrtKey])
      privCrtKey2.map(_.getPublicExponent) shouldBe Base64String(e, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey2.map(_.getPrimeP) shouldBe Base64String(p, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey2.map(_.getPrimeQ) shouldBe Base64String(q, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey2.map(_.getPrimeExponentP) shouldBe Base64String(dp, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey2.map(_.getPrimeExponentQ) shouldBe Base64String(dq, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey2.map(_.getCrtCoefficient) shouldBe Base64String(qi, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      // Key pair import, 1st private form
      val key2 = RSAJWK.fromPublicKeyWithPrivateKey(
        rsaPublicKey = pubKey.getOrElse(???),
        rsaPrivateKey = privKey2.getOrElse(???),
        publicKeyUseType = Some(PublicKeyUseType.Signature),
        keyOperations = KeyOperations.empty,
        algorithmType = Some(JWSAlgorithmType.RS256),
        keyId = Some(KeyId("1")),
        x509Url = None,
        x509CertificateSHA1Thumbprint = None,
        x509CertificateSHA256Thumbprint = None,
        x509CertificateChain = None
      )

      key2.map(_.publicKeyUseType) shouldBe Right(Some(PublicKeyUseType.Signature))
      key2.map(_.keyOperations) shouldBe Right(KeyOperations.empty)
      key2.map(_.algorithmType) shouldBe Right(Some(JWSAlgorithmType.RS256))
      key2.map(_.keyId) shouldBe Right(Some(KeyId("1")))

      key2.map(_.modulus) shouldBe Right(Base64String(n, urlSafe = true))
      key2.map(_.publicExponent) shouldBe Right(Base64String(e, urlSafe = true))

      key2.map(_.privateExponent) shouldBe Right(Some(Base64String(d, urlSafe = true)))

      key2.fold(_ => false, _.firstPrimeFactor.isEmpty)
      key2.fold(_ => false, _.secondPrimeFactor.isEmpty)

      key2.fold(_ => false, _.firstFactorCRTExponent.isEmpty)
      key2.fold(_ => false, _.secondFactorCRTExponent.isEmpty)

      key2.fold(_ => false, _.firstCRTCoefficient.isEmpty)

      key2.fold(_ => false, _.isPrivate)

      // Key pair import, 2nd private form
      val key3 = RSAJWK.fromPublicKeyWithPrivateCrtKey(
        pubKey.getOrElse(???),
        privCrtKey2.getOrElse(???),
        publicKeyUseType = Some(PublicKeyUseType.Signature),
        keyOperations = KeyOperations.empty,
        algorithmType = Some(JWSAlgorithmType.RS256),
        keyId = Some(KeyId("1")),
        x509Url = None,
        x509CertificateSHA1Thumbprint = None,
        x509CertificateSHA256Thumbprint = None,
        x509CertificateChain = None
      )

      key3.map(_.publicKeyUseType) shouldBe Right(Some(PublicKeyUseType.Signature))
      key3.map(_.keyOperations) shouldBe Right(KeyOperations.empty)
      key3.map(_.algorithmType) shouldBe Right(Some(JWSAlgorithmType.RS256))
      key3.map(_.keyId) shouldBe Right(Some(KeyId("1")))

      key3.map(_.modulus) shouldBe Right(Base64String(n, urlSafe = true))
      key3.map(_.publicExponent) shouldBe Right(Base64String(e, urlSafe = true))

      key3.map(_.privateExponent) shouldBe Right(Some(Base64String(d, urlSafe = true)))

      key3.map(_.firstPrimeFactor) shouldBe Right(Some(Base64String(p, urlSafe = true)))
      key3.map(_.secondPrimeFactor) shouldBe Right(Some(Base64String(q, urlSafe = true)))

      key3.map(_.firstFactorCRTExponent) shouldBe Right(Some(Base64String(dp, urlSafe = true)))
      key3.map(_.secondFactorCRTExponent) shouldBe Right(Some(Base64String(dq, urlSafe = true)))

      key3.map(_.firstCRTCoefficient) shouldBe Right(Some(Base64String(qi, urlSafe = true)))

      key2.fold(_ => false, _.isPrivate)
    }
    "should be able to export & import PublicKey" in {
      val key = new RSAJWK(n = Base64String(n, urlSafe = true), e = Base64String(e, urlSafe = true))
      key.isInstanceOf[AssymetricJWK]
      val pubKey = key.toPublicKey.map(_.asInstanceOf[RSAPublicKey])
      pubKey.map(_.getModulus) shouldBe Base64String(n, urlSafe = true).decodeToBigInt.map(_.bigInteger)
      pubKey.map(_.getPublicExponent) shouldBe Base64String(e, urlSafe = true).decodeToBigInt.map(_.bigInteger)
      pubKey.map(_.getAlgorithm) shouldBe Right("RSA")

      val key2 = pubKey.flatMap { key => RSAJWK.fromRSAPublicKey(key) }
      key2.map(_.modulus) shouldBe Right(Base64String(n, urlSafe = true))
      key2.map(_.publicExponent) shouldBe Right(Base64String(e, urlSafe = true))
    }
    "should be able to export PrivateKey" in {
      val key = new RSAJWK(
        n = Base64String(n, urlSafe = true),
        e = Base64String(e, urlSafe = true),
        publicKeyUseType = Some(PublicKeyUseType.Signature),
        keyOperations = KeyOperations.empty,
        algorithmType = Some(JWSAlgorithmType.RS256),
        keyId = Some(KeyId("1")),
        x509Url = None,
        x509CertificateSHA1Thumbprint = None,
        x509CertificateSHA256Thumbprint = None,
        x509CertificateChain = None,
        d = Some(Base64String(d, urlSafe = true)),
        p = Some(Base64String(p, urlSafe = true)),
        q = Some(Base64String(q, urlSafe = true)),
        dp = Some(Base64String(dp, urlSafe = true)),
        dq = Some(Base64String(dq, urlSafe = true)),
        qi = Some(Base64String(qi, urlSafe = true))
      )
      key.isInstanceOf[AssymetricJWK]
      val privKey1 = key.toPrivateKey.map(_.asInstanceOf[RSAPrivateKey])
      privKey1.map(_.getModulus) shouldBe Base64String(n, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privKey1.map(_.getPrivateExponent) shouldBe Base64String(d, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      val privCrtKey = privKey1.map(_.asInstanceOf[RSAPrivateCrtKey])
      privCrtKey.map(_.getPrimeP) shouldBe Base64String(p, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey.map(_.getPrimeQ) shouldBe Base64String(q, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey.map(_.getPrimeExponentP) shouldBe Base64String(dp, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey.map(_.getPrimeExponentQ) shouldBe Base64String(dq, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
      privCrtKey.map(_.getCrtCoefficient) shouldBe Base64String(qi, urlSafe = true).decodeToBigInt
        .map(_.bigInteger)
    }
    "should be able to parse some key" in {
      val jsonText = "{\n" +
        "      \"kty\": \"RSA\",\n" +
        "      \"n\": \"f9BhJgBgoDKGcYLh+xl6qulS8fUFYxuWSz4Sk+7Yw2Wv4Wroe3yLzJjqEqH8IFR0Ow8Sr3pZo0IwOPcWHQZMQr0s2kWbKSpBrnDsK4vsdBvoP1jOaylA9XsHPF9EZ/1F+eQkVHoMsc9eccf0nmr3ubD56LjSorTsbOuxi8nqEzisvhDHthacW/qxbpR/jojQNfdWyDz6NC+MA2LYYpdsw5TG8AVdKjobHWfQvXYdcpvQRkDDhgbwQt1KD8ZJ1VL+nJcIfSppPzCbfM2eY78y/c4euL/SQPs7kGf+u3R9hden7FjMUuIFZoAictiBgjVZ/JOaK+C++L+IsnCKqauhEQ==\",\n" +
        "      \"e\": \"AQAB\",\n" +
        "      \"alg\": \"RS256\"\n" +
        "}"

      val key = parse(jsonText).flatMap { json => json.as[RSAJWK] }

      key.map(_.algorithmType) == Right(Some(JWSAlgorithmType.RS256))
    }
    "should be able to key conversion of round trip" in {
      val keyGen = KeyPairGenerator.getInstance("RSA")
      keyGen.initialize(512)
      val keyPair         = keyGen.genKeyPair
      val rsaPublicKeyIn  = keyPair.getPublic.asInstanceOf[RSAPublicKey]
      val rsaPrivateKeyIn = keyPair.getPrivate.asInstanceOf[RSAPrivateKey]
      val rsaJWK          = RSAJWK.fromKeyPair(rsaPublicKeyIn, rsaPrivateKeyIn)
      // Compare JWK values with original Java RSA values
      rsaJWK.flatMap(_.publicExponent.decodeToBigInt.map(_.bigInteger)) shouldBe Right(
        rsaPublicKeyIn.getPublicExponent
      )
      rsaJWK.flatMap(_.modulus.decodeToBigInt.map(_.bigInteger)) shouldBe Right(rsaPublicKeyIn.getModulus)
      rsaJWK.map(_.privateExponent.map(_.decodeToBigInt.map(_.bigInteger))) shouldBe Right(
        Some(Right(rsaPrivateKeyIn.getPrivateExponent))
      )

      // Convert back to Java RSA keys
      val rsaPublicKeyOut  = rsaJWK.map(_.toRSAPublicKey)
      val rsaPrivateKeyOut = rsaJWK.map(_.toRSAPrivateKey)

      rsaPublicKeyOut.flatMap(_.map(_.getAlgorithm)) shouldBe Right(rsaPublicKeyIn.getAlgorithm)
      rsaPublicKeyOut.flatMap(_.map(_.getPublicExponent)) shouldBe Right(rsaPublicKeyIn.getPublicExponent)
      rsaPublicKeyOut.flatMap(_.map(_.getModulus)) shouldBe Right(rsaPublicKeyIn.getModulus)

      rsaPrivateKeyOut.flatMap(_.map(_.map(_.getAlgorithm))) shouldBe Right(Some(rsaPrivateKeyIn.getAlgorithm))
      rsaPrivateKeyOut.flatMap(_.map(_.map(_.getPrivateExponent))) shouldBe Right(
        Some(rsaPrivateKeyIn.getPrivateExponent)
      )

      // Compare encoded forms
      base64StringFactory
        .encode(rsaPublicKeyIn.getEncoded)
        .map(_.asString) shouldBe base64StringFactory
        .encode(rsaPublicKeyOut.flatMap(identity).getOrElse(???).getEncoded)
        .map(_.asString)
      base64StringFactory
        .encode(rsaPrivateKeyIn.getEncoded)
        .map(_.asString) shouldBe base64StringFactory
        .encode(rsaPrivateKeyOut.flatMap(_.flatMap(_.toRight(???))).getOrElse(???).getEncoded)
        .map(_.asString)
      val key2 = RSAJWK.fromKeyPair(
        rsaPublicKeyOut.flatMap(identity).getOrElse(???),
        rsaPrivateKeyOut.flatMap(_.flatMap(_.toRight(???))).getOrElse(???)
      )
      key2.flatMap(_.publicExponent.decodeToBigInt.map(_.bigInteger)) shouldBe Right(rsaPublicKeyIn.getPublicExponent)
      key2.flatMap(_.modulus.decodeToBigInt.map(_.bigInteger)) shouldBe Right(rsaPublicKeyIn.getModulus)
      key2.map(_.privateExponent.map(_.decodeToBigInt.map(_.bigInteger))) shouldBe Right(
        Some(Right(rsaPrivateKeyIn.getPrivateExponent))
      )
    }
    "should be able to parse cookbook example" in {
      val jsonText = "{" +
        "\"kty\": \"RSA\"," +
        "\"kid\": \"bilbo.baggins@hobbiton.example\"," +
        "\"use\": \"sig\"," +
        "\"n\": \"n4EPtAOCc9AlkeQHPzHStgAbgs7bTZLwUBZdR8_KuKPEHLd4rHVTeT" +
        "-O-XV2jRojdNhxJWTDvNd7nqQ0VEiZQHz_AJmSCpMaJMRBSFKrKb2wqV" +
        "wGU_NsYOYL-QtiWN2lbzcEe6XC0dApr5ydQLrHqkHHig3RBordaZ6Aj-" +
        "oBHqFEHYpPe7Tpe-OfVfHd1E6cS6M1FZcD1NNLYD5lFHpPI9bTwJlsde" +
        "3uhGqC0ZCuEHg8lhzwOHrtIQbS0FVbb9k3-tVTU4fg_3L_vniUFAKwuC" +
        "LqKnS2BYwdq_mzSnbLY7h_qixoR7jig3__kRhuaxwUkRz5iaiQkqgc5g" +
        "HdrNP5zw\"," +
        "\"e\": \"AQAB\"," +
        "\"d\": \"bWUC9B-EFRIo8kpGfh0ZuyGPvMNKvYWNtB_ikiH9k20eT-O1q_I78e" +
        "iZkpXxXQ0UTEs2LsNRS-8uJbvQ-A1irkwMSMkK1J3XTGgdrhCku9gRld" +
        "Y7sNA_AKZGh-Q661_42rINLRCe8W-nZ34ui_qOfkLnK9QWDDqpaIsA-b" +
        "MwWWSDFu2MUBYwkHTMEzLYGqOe04noqeq1hExBTHBOBdkMXiuFhUq1BU" +
        "6l-DqEiWxqg82sXt2h-LMnT3046AOYJoRioz75tSUQfGCshWTBnP5uDj" +
        "d18kKhyv07lhfSJdrPdM5Plyl21hsFf4L_mHCuoFau7gdsPfHPxxjVOc" +
        "OpBrQzwQ\"," +
        "\"p\": \"3Slxg_DwTXJcb6095RoXygQCAZ5RnAvZlno1yhHtnUex_fp7AZ_9nR" +
        "aO7HX_-SFfGQeutao2TDjDAWU4Vupk8rw9JR0AzZ0N2fvuIAmr_WCsmG" +
        "peNqQnev1T7IyEsnh8UMt-n5CafhkikzhEsrmndH6LxOrvRJlsPp6Zv8" +
        "bUq0k\"," +
        "\"q\": \"uKE2dh-cTf6ERF4k4e_jy78GfPYUIaUyoSSJuBzp3Cubk3OCqs6grT" +
        "8bR_cu0Dm1MZwWmtdqDyI95HrUeq3MP15vMMON8lHTeZu2lmKvwqW7an" +
        "V5UzhM1iZ7z4yMkuUwFWoBvyY898EXvRD-hdqRxHlSqAZ192zB3pVFJ0" +
        "s7pFc\"," +
        "\"dp\": \"B8PVvXkvJrj2L-GYQ7v3y9r6Kw5g9SahXBwsWUzp19TVlgI-YV85q" +
        "1NIb1rxQtD-IsXXR3-TanevuRPRt5OBOdiMGQp8pbt26gljYfKU_E9xn" +
        "-RULHz0-ed9E9gXLKD4VGngpz-PfQ_q29pk5xWHoJp009Qf1HvChixRX" +
        "59ehik\"," +
        "\"dq\": \"CLDmDGduhylc9o7r84rEUVn7pzQ6PF83Y-iBZx5NT-TpnOZKF1pEr" +
        "AMVeKzFEl41DlHHqqBLSM0W1sOFbwTxYWZDm6sI6og5iTbwQGIC3gnJK" +
        "bi_7k_vJgGHwHxgPaX2PnvP-zyEkDERuf-ry4c_Z11Cq9AqC2yeL6kdK" +
        "T1cYF8\"," +
        "\"qi\": \"3PiqvXQN0zwMeE-sBvZgi289XP9XCQF3VWqPzMKnIgQp7_Tugo6-N" +
        "ZBKCQsMf3HaEGBjTVJs_jcK8-TRXvaKe-7ZMaQj8VfBdYkssbu0NKDDh" +
        "jJ-GtiseaDVWt7dcH0cfwxgFUHpQh7FoCrjFJ6h6ZEpMF6xmujs4qMpP" +
        "z8aaI4\"" +
        "}"
      val jwk = parse(jsonText).flatMap { json => json.as[RSAJWK] }
      jwk.map(_.keyType) shouldBe Right(KeyType.RSA)
      jwk.map(_.keyId) shouldBe Right(Some(KeyId("bilbo.baggins@hobbiton.example")))
      jwk.map(_.publicKeyUseType) shouldBe Right(Some(PublicKeyUseType.Signature))
      jwk.map(_.modulus.asString) shouldBe Right(
        "n4EPtAOCc9AlkeQHPzHStgAbgs7bTZLwUBZdR8_KuKPEHLd4rHVTeT" +
        "-O-XV2jRojdNhxJWTDvNd7nqQ0VEiZQHz_AJmSCpMaJMRBSFKrKb2wqV" +
        "wGU_NsYOYL-QtiWN2lbzcEe6XC0dApr5ydQLrHqkHHig3RBordaZ6Aj-" +
        "oBHqFEHYpPe7Tpe-OfVfHd1E6cS6M1FZcD1NNLYD5lFHpPI9bTwJlsde" +
        "3uhGqC0ZCuEHg8lhzwOHrtIQbS0FVbb9k3-tVTU4fg_3L_vniUFAKwuC" +
        "LqKnS2BYwdq_mzSnbLY7h_qixoR7jig3__kRhuaxwUkRz5iaiQkqgc5g" +
        "HdrNP5zw"
      )
      jwk.map(_.publicExponent.asString) shouldBe Right("AQAB")
      jwk.map(_.privateExponent.map(_.asString)) shouldBe Right(
        Some(
          "bWUC9B-EFRIo8kpGfh0ZuyGPvMNKvYWNtB_ikiH9k20eT-O1q_I78e" +
          "iZkpXxXQ0UTEs2LsNRS-8uJbvQ-A1irkwMSMkK1J3XTGgdrhCku9gRld" +
          "Y7sNA_AKZGh-Q661_42rINLRCe8W-nZ34ui_qOfkLnK9QWDDqpaIsA-b" +
          "MwWWSDFu2MUBYwkHTMEzLYGqOe04noqeq1hExBTHBOBdkMXiuFhUq1BU" +
          "6l-DqEiWxqg82sXt2h-LMnT3046AOYJoRioz75tSUQfGCshWTBnP5uDj" +
          "d18kKhyv07lhfSJdrPdM5Plyl21hsFf4L_mHCuoFau7gdsPfHPxxjVOc" +
          "OpBrQzwQ"
        )
      )
      jwk.map(_.firstPrimeFactor.map(_.asString)) shouldBe Right(
        Some(
          "3Slxg_DwTXJcb6095RoXygQCAZ5RnAvZlno1yhHtnUex_fp7AZ_9nR" +
          "aO7HX_-SFfGQeutao2TDjDAWU4Vupk8rw9JR0AzZ0N2fvuIAmr_WCsmG" +
          "peNqQnev1T7IyEsnh8UMt-n5CafhkikzhEsrmndH6LxOrvRJlsPp6Zv8" +
          "bUq0k"
        )
      )
      jwk.map(_.secondPrimeFactor.map(_.asString)) shouldBe Right(
        Some(
          "uKE2dh-cTf6ERF4k4e_jy78GfPYUIaUyoSSJuBzp3Cubk3OCqs6grT" +
          "8bR_cu0Dm1MZwWmtdqDyI95HrUeq3MP15vMMON8lHTeZu2lmKvwqW7an" +
          "V5UzhM1iZ7z4yMkuUwFWoBvyY898EXvRD-hdqRxHlSqAZ192zB3pVFJ0" +
          "s7pFc"
        )
      )
      jwk.map(_.firstFactorCRTExponent.map(_.asString)) shouldBe Right(
        Some(
          "B8PVvXkvJrj2L-GYQ7v3y9r6Kw5g9SahXBwsWUzp19TVlgI-YV85q" +
          "1NIb1rxQtD-IsXXR3-TanevuRPRt5OBOdiMGQp8pbt26gljYfKU_E9xn" +
          "-RULHz0-ed9E9gXLKD4VGngpz-PfQ_q29pk5xWHoJp009Qf1HvChixRX" +
          "59ehik"
        )
      )
      jwk.map(_.secondFactorCRTExponent.map(_.asString)) shouldBe Right(
        Some(
          "CLDmDGduhylc9o7r84rEUVn7pzQ6PF83Y-iBZx5NT-TpnOZKF1pEr" +
          "AMVeKzFEl41DlHHqqBLSM0W1sOFbwTxYWZDm6sI6og5iTbwQGIC3gnJK" +
          "bi_7k_vJgGHwHxgPaX2PnvP-zyEkDERuf-ry4c_Z11Cq9AqC2yeL6kdK" +
          "T1cYF8"
        )
      )
      jwk.map(_.firstCRTCoefficient.map(_.asString)) shouldBe Right(
        Some(
          "3PiqvXQN0zwMeE-sBvZgi289XP9XCQF3VWqPzMKnIgQp7_Tugo6-N" +
          "ZBKCQsMf3HaEGBjTVJs_jcK8-TRXvaKe-7ZMaQj8VfBdYkssbu0NKDDh" +
          "jJ-GtiseaDVWt7dcH0cfwxgFUHpQh7FoCrjFJ6h6ZEpMF6xmujs4qMpP" +
          "z8aaI4"
        )
      )
      // Convert to Java RSA key object// Convert to Java RSA key object
      val jwk2 = for {
        rsaPublicKey  <- jwk.flatMap(_.toRSAPublicKey)
        rsaPrivateKey <- jwk.flatMap(_.toRSAPrivateKey)
        result        <- RSAJWK.fromKeyPair(rsaPublicKey, rsaPrivateKey.get)
      } yield result

      jwk2.map(_.modulus.asString) shouldBe Right(
        "n4EPtAOCc9AlkeQHPzHStgAbgs7bTZLwUBZdR8_KuKPEHLd4rHVTeT" +
        "-O-XV2jRojdNhxJWTDvNd7nqQ0VEiZQHz_AJmSCpMaJMRBSFKrKb2wqV" +
        "wGU_NsYOYL-QtiWN2lbzcEe6XC0dApr5ydQLrHqkHHig3RBordaZ6Aj-" +
        "oBHqFEHYpPe7Tpe-OfVfHd1E6cS6M1FZcD1NNLYD5lFHpPI9bTwJlsde" +
        "3uhGqC0ZCuEHg8lhzwOHrtIQbS0FVbb9k3-tVTU4fg_3L_vniUFAKwuC" +
        "LqKnS2BYwdq_mzSnbLY7h_qixoR7jig3__kRhuaxwUkRz5iaiQkqgc5g" +
        "HdrNP5zw"
      )
      jwk2.map(_.publicExponent.asString) shouldBe Right("AQAB")
      jwk2.map(_.privateExponent.map(_.asString)) shouldBe Right(
        Some(
          "bWUC9B-EFRIo8kpGfh0ZuyGPvMNKvYWNtB_ikiH9k20eT-O1q_I78e" +
          "iZkpXxXQ0UTEs2LsNRS-8uJbvQ-A1irkwMSMkK1J3XTGgdrhCku9gRld" +
          "Y7sNA_AKZGh-Q661_42rINLRCe8W-nZ34ui_qOfkLnK9QWDDqpaIsA-b" +
          "MwWWSDFu2MUBYwkHTMEzLYGqOe04noqeq1hExBTHBOBdkMXiuFhUq1BU" +
          "6l-DqEiWxqg82sXt2h-LMnT3046AOYJoRioz75tSUQfGCshWTBnP5uDj" +
          "d18kKhyv07lhfSJdrPdM5Plyl21hsFf4L_mHCuoFau7gdsPfHPxxjVOc" +
          "OpBrQzwQ"
        )
      )
    }
    "should be able to parse cookbook example2" in {
      val jsonText = "{" +
        "\"kty\":\"RSA\"," +
        "\"kid\":\"frodo.baggins@hobbiton.example\"," +
        "\"use\":\"enc\"," +
        "\"n\":\"maxhbsmBtdQ3CNrKvprUE6n9lYcregDMLYNeTAWcLj8NnPU9XIYegT" +
        "HVHQjxKDSHP2l-F5jS7sppG1wgdAqZyhnWvXhYNvcM7RfgKxqNx_xAHx" +
        "6f3yy7s-M9PSNCwPC2lh6UAkR4I00EhV9lrypM9Pi4lBUop9t5fS9W5U" +
        "NwaAllhrd-osQGPjIeI1deHTwx-ZTHu3C60Pu_LJIl6hKn9wbwaUmA4c" +
        "R5Bd2pgbaY7ASgsjCUbtYJaNIHSoHXprUdJZKUMAzV0WOKPfA6OPI4oy" +
        "pBadjvMZ4ZAj3BnXaSYsEZhaueTXvZB4eZOAjIyh2e_VOIKVMsnDrJYA" +
        "VotGlvMQ\"," +
        "\"e\":\"AQAB\"," +
        "\"d\":\"Kn9tgoHfiTVi8uPu5b9TnwyHwG5dK6RE0uFdlpCGnJN7ZEi963R7wy" +
        "bQ1PLAHmpIbNTztfrheoAniRV1NCIqXaW_qS461xiDTp4ntEPnqcKsyO" +
        "5jMAji7-CL8vhpYYowNFvIesgMoVaPRYMYT9TW63hNM0aWs7USZ_hLg6" +
        "Oe1mY0vHTI3FucjSM86Nff4oIENt43r2fspgEPGRrdE6fpLc9Oaq-qeP" +
        "1GFULimrRdndm-P8q8kvN3KHlNAtEgrQAgTTgz80S-3VD0FgWfgnb1PN" +
        "miuPUxO8OpI9KDIfu_acc6fg14nsNaJqXe6RESvhGPH2afjHqSy_Fd2v" +
        "pzj85bQQ\"," +
        "\"p\":\"2DwQmZ43FoTnQ8IkUj3BmKRf5Eh2mizZA5xEJ2MinUE3sdTYKSLtaE" +
        "oekX9vbBZuWxHdVhM6UnKCJ_2iNk8Z0ayLYHL0_G21aXf9-unynEpUsH" +
        "7HHTklLpYAzOOx1ZgVljoxAdWNn3hiEFrjZLZGS7lOH-a3QQlDDQoJOJ" +
        "2VFmU\"," +
        "\"q\":\"te8LY4-W7IyaqH1ExujjMqkTAlTeRbv0VLQnfLY2xINnrWdwiQ93_V" +
        "F099aP1ESeLja2nw-6iKIe-qT7mtCPozKfVtUYfz5HrJ_XY2kfexJINb" +
        "9lhZHMv5p1skZpeIS-GPHCC6gRlKo1q-idn_qxyusfWv7WAxlSVfQfk8" +
        "d6Et0\"," +
        "\"dp\":\"UfYKcL_or492vVc0PzwLSplbg4L3-Z5wL48mwiswbpzOyIgd2xHTH" +
        "QmjJpFAIZ8q-zf9RmgJXkDrFs9rkdxPtAsL1WYdeCT5c125Fkdg317JV" +
        "RDo1inX7x2Kdh8ERCreW8_4zXItuTl_KiXZNU5lvMQjWbIw2eTx1lpsf" +
        "lo0rYU\"," +
        "\"dq\":\"iEgcO-QfpepdH8FWd7mUFyrXdnOkXJBCogChY6YKuIHGc_p8Le9Mb" +
        "pFKESzEaLlN1Ehf3B6oGBl5Iz_ayUlZj2IoQZ82znoUrpa9fVYNot87A" +
        "CfzIG7q9Mv7RiPAderZi03tkVXAdaBau_9vs5rS-7HMtxkVrxSUvJY14" +
        "TkXlHE\"," +
        "\"qi\":\"kC-lzZOqoFaZCr5l0tOVtREKoVqaAYhQiqIRGL-MzS4sCmRkxm5vZ" +
        "lXYx6RtE1n_AagjqajlkjieGlxTTThHD8Iga6foGBMaAr5uR1hGQpSc7" +
        "Gl7CF1DZkBJMTQN6EshYzZfxW08mIO8M6Rzuh0beL6fG9mkDcIyPrBXx" +
        "2bQ_mM\"" +
        "}"
      val jwk = parse(jsonText).flatMap { json => json.as[RSAJWK] }
      jwk.map(_.keyType) shouldBe Right(KeyType.RSA)
      jwk.map(_.keyId) shouldBe Right(Some(KeyId("frodo.baggins@hobbiton.example")))
      jwk.map(_.publicKeyUseType) shouldBe Right(Some(PublicKeyUseType.Encryption))

      jwk.map(_.modulus.asString) shouldBe Right(
        "maxhbsmBtdQ3CNrKvprUE6n9lYcregDMLYNeTAWcLj8NnPU9XIYegT" +
        "HVHQjxKDSHP2l-F5jS7sppG1wgdAqZyhnWvXhYNvcM7RfgKxqNx_xAHx" +
        "6f3yy7s-M9PSNCwPC2lh6UAkR4I00EhV9lrypM9Pi4lBUop9t5fS9W5U" +
        "NwaAllhrd-osQGPjIeI1deHTwx-ZTHu3C60Pu_LJIl6hKn9wbwaUmA4c" +
        "R5Bd2pgbaY7ASgsjCUbtYJaNIHSoHXprUdJZKUMAzV0WOKPfA6OPI4oy" +
        "pBadjvMZ4ZAj3BnXaSYsEZhaueTXvZB4eZOAjIyh2e_VOIKVMsnDrJYA" +
        "VotGlvMQ"
      )
      jwk.map(_.publicExponent.asString) shouldBe Right("AQAB")
      jwk.map(_.privateExponent.map(_.asString)) shouldBe Right(
        Some(
          "Kn9tgoHfiTVi8uPu5b9TnwyHwG5dK6RE0uFdlpCGnJN7ZEi963R7wy" +
          "bQ1PLAHmpIbNTztfrheoAniRV1NCIqXaW_qS461xiDTp4ntEPnqcKsyO" +
          "5jMAji7-CL8vhpYYowNFvIesgMoVaPRYMYT9TW63hNM0aWs7USZ_hLg6" +
          "Oe1mY0vHTI3FucjSM86Nff4oIENt43r2fspgEPGRrdE6fpLc9Oaq-qeP" +
          "1GFULimrRdndm-P8q8kvN3KHlNAtEgrQAgTTgz80S-3VD0FgWfgnb1PN" +
          "miuPUxO8OpI9KDIfu_acc6fg14nsNaJqXe6RESvhGPH2afjHqSy_Fd2v" +
          "pzj85bQQ"
        )
      )

      jwk.map(_.firstPrimeFactor.map(_.asString)) shouldBe Right(
        Some(
          "2DwQmZ43FoTnQ8IkUj3BmKRf5Eh2mizZA5xEJ2MinUE3sdTYKSLtaE" +
          "oekX9vbBZuWxHdVhM6UnKCJ_2iNk8Z0ayLYHL0_G21aXf9-unynEpUsH" +
          "7HHTklLpYAzOOx1ZgVljoxAdWNn3hiEFrjZLZGS7lOH-a3QQlDDQoJOJ" +
          "2VFmU"
        )
      )
      jwk.map(_.secondPrimeFactor.map(_.asString)) shouldBe Right(
        Some(
          "te8LY4-W7IyaqH1ExujjMqkTAlTeRbv0VLQnfLY2xINnrWdwiQ93_V" +
          "F099aP1ESeLja2nw-6iKIe-qT7mtCPozKfVtUYfz5HrJ_XY2kfexJINb" +
          "9lhZHMv5p1skZpeIS-GPHCC6gRlKo1q-idn_qxyusfWv7WAxlSVfQfk8" +
          "d6Et0"
        )
      )
      jwk.map(_.firstFactorCRTExponent.map(_.asString)) shouldBe Right(
        Some(
          "UfYKcL_or492vVc0PzwLSplbg4L3-Z5wL48mwiswbpzOyIgd2xHTH" +
          "QmjJpFAIZ8q-zf9RmgJXkDrFs9rkdxPtAsL1WYdeCT5c125Fkdg317JV" +
          "RDo1inX7x2Kdh8ERCreW8_4zXItuTl_KiXZNU5lvMQjWbIw2eTx1lpsf" +
          "lo0rYU"
        )
      )
      jwk.map(_.secondFactorCRTExponent.map(_.asString)) shouldBe Right(
        Some(
          "iEgcO-QfpepdH8FWd7mUFyrXdnOkXJBCogChY6YKuIHGc_p8Le9Mb" +
          "pFKESzEaLlN1Ehf3B6oGBl5Iz_ayUlZj2IoQZ82znoUrpa9fVYNot87A" +
          "CfzIG7q9Mv7RiPAderZi03tkVXAdaBau_9vs5rS-7HMtxkVrxSUvJY14" +
          "TkXlHE"
        )
      )
      jwk.map(_.firstCRTCoefficient.map(_.asString)) shouldBe Right(
        Some(
          "kC-lzZOqoFaZCr5l0tOVtREKoVqaAYhQiqIRGL-MzS4sCmRkxm5vZ" +
          "lXYx6RtE1n_AagjqajlkjieGlxTTThHD8Iga6foGBMaAr5uR1hGQpSc7" +
          "Gl7CF1DZkBJMTQN6EshYzZfxW08mIO8M6Rzuh0beL6fG9mkDcIyPrBXx" +
          "2bQ_mM"
        )
      )
      // Convert to Java RSA key object
      val jwk2 = for {
        rsaPublicKey  <- jwk.flatMap(_.toRSAPublicKey)
        rsaPrivateKey <- jwk.flatMap(_.toRSAPrivateKey)
        result        <- RSAJWK.fromKeyPair(rsaPublicKey, rsaPrivateKey.get)
      } yield result
      jwk2.map(_.modulus.asString) shouldBe Right(
        "maxhbsmBtdQ3CNrKvprUE6n9lYcregDMLYNeTAWcLj8NnPU9XIYegT" +
        "HVHQjxKDSHP2l-F5jS7sppG1wgdAqZyhnWvXhYNvcM7RfgKxqNx_xAHx" +
        "6f3yy7s-M9PSNCwPC2lh6UAkR4I00EhV9lrypM9Pi4lBUop9t5fS9W5U" +
        "NwaAllhrd-osQGPjIeI1deHTwx-ZTHu3C60Pu_LJIl6hKn9wbwaUmA4c" +
        "R5Bd2pgbaY7ASgsjCUbtYJaNIHSoHXprUdJZKUMAzV0WOKPfA6OPI4oy" +
        "pBadjvMZ4ZAj3BnXaSYsEZhaueTXvZB4eZOAjIyh2e_VOIKVMsnDrJYA" +
        "VotGlvMQ"
      )
      jwk2.map(_.publicExponent.asString) shouldBe Right("AQAB")
      jwk2.map(_.privateExponent.map(_.asString)) shouldBe Right(
        Some(
          "Kn9tgoHfiTVi8uPu5b9TnwyHwG5dK6RE0uFdlpCGnJN7ZEi963R7wy" +
          "bQ1PLAHmpIbNTztfrheoAniRV1NCIqXaW_qS461xiDTp4ntEPnqcKsyO" +
          "5jMAji7-CL8vhpYYowNFvIesgMoVaPRYMYT9TW63hNM0aWs7USZ_hLg6" +
          "Oe1mY0vHTI3FucjSM86Nff4oIENt43r2fspgEPGRrdE6fpLc9Oaq-qeP" +
          "1GFULimrRdndm-P8q8kvN3KHlNAtEgrQAgTTgz80S-3VD0FgWfgnb1PN" +
          "miuPUxO8OpI9KDIfu_acc6fg14nsNaJqXe6RESvhGPH2afjHqSy_Fd2v" +
          "pzj85bQQ"
        )
      )
    }
    "should be able to compute a thumbprint" in {
      val jsonText = "{\"e\":\"AQAB\",\"kty\":\"RSA\",\"n\":\"0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2" +
        "aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCi" +
        "FV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65Y" +
        "GjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n" +
        "91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_x" +
        "BniIqbw0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw\"}"
      val jwk        = parse(jsonText).flatMap { json => json.as[RSAJWK] }
      val thumbprint = jwk.flatMap(_.computeThumbprint)
      thumbprint shouldBe jwk.flatMap(_.computeThumbprint("SHA-256"))
      thumbprint shouldBe Right(Base64String("NzbLsXh8uDCcd-6MNwXF4W_7noWXFZAfHkxZsRGC9Xs"))
    }

  }
}
