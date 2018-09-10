package com.chatwork.scala.jwk

import java.net.URI
import java.security.interfaces.{ECPrivateKey, ECPublicKey}
import java.security.spec.{ECPoint, ECPrivateKeySpec, ECPublicKeySpec, InvalidKeySpecException}
import java.security.{KeyFactory, NoSuchAlgorithmException, _}
import java.time.ZonedDateTime

import cats.implicits._
import com.chatwork.scala.jwk.JWKError._
import com.chatwork.scala.jwk.utils.ECChecks
import com.github.j5ik2o.base64scala.{Base64EncodeError, Base64String, Base64StringFactory, BigIntUtils}
import io.circe._

object ECJWK extends ECJWKJsonImplicits {

  def parseFromJson(json: Json): Either[JWKCreationError, ECJWK] =
    json.as[ECJWK].leftMap(error => JWKCreationError(error.getMessage, None))

  def parseFromText(jsonText: String): Either[JWKCreationError, ECJWK] = {
    parser
      .parse(jsonText) match {
      case Left(error) =>
        Left(JWKCreationError(error.getMessage()))
      case Right(json) =>
        parseFromJson(json)
    }
  }

  def apply(curve: Curve,
            x: Base64String,
            y: Base64String,
            publicKeyUseType: Option[PublicKeyUseType] = None,
            keyOperations: KeyOperations = KeyOperations.empty,
            algorithmType: Option[JWSAlgorithmType] = None,
            keyId: Option[KeyId] = None,
            x509Url: Option[URI] = None,
            x509CertificateSHA1Thumbprint: Option[Base64String] = None,
            x509CertificateSHA256Thumbprint: Option[Base64String] = None,
            x509CertificateChain: List[Base64String] = List.empty,
            d: Option[Base64String] = None,
            privateKey: Option[PrivateKey] = None,
            expireAt: Option[ZonedDateTime] = None,
            keyStore: Option[KeyStore] = None): Either[JWKCreationError, ECJWK] = {
    try {
      Right(
        new ECJWK(
          curve,
          x,
          y,
          publicKeyUseType,
          keyOperations,
          algorithmType,
          keyId,
          x509Url,
          x509CertificateSHA1Thumbprint,
          x509CertificateSHA256Thumbprint,
          x509CertificateChain,
          d,
          privateKey,
          expireAt
        )
      )
    } catch {
      case ex: IllegalArgumentException =>
        Left(JWKCreationError(ex.getMessage))
    }
  }

  def fromKeyPair(curve: Curve,
                  pub: ECPublicKey,
                  priv: ECPrivateKey,
                  publicKeyUseType: Option[PublicKeyUseType] = None,
                  keyOperations: KeyOperations = KeyOperations.empty,
                  algorithmType: Option[JWSAlgorithmType] = None,
                  keyId: Option[KeyId] = None,
                  x509Url: Option[URI] = None,
                  x509CertificateSHA1Thumbprint: Option[Base64String] = None,
                  x509CertificateSHA256Thumbprint: Option[Base64String] = None,
                  x509CertificateChain: List[Base64String] = List.empty,
                  expireAt: Option[ZonedDateTime] = None): Either[JWKCreationError, ECJWK] = {
    for {
      pub <- encodeCoordinate(pub.getParams.getCurve.getField.getFieldSize, pub.getW.getAffineX())
        .leftMap(error => JWKCreationError(error.message))
      priv <- encodeCoordinate(priv.getParams.getCurve.getField.getFieldSize, priv.getS())
        .leftMap(error => JWKCreationError(error.message))
      jwk <- apply(
        curve,
        pub,
        priv,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA1Thumbprint,
        x509CertificateSHA256Thumbprint,
        x509CertificateChain,
        expireAt = expireAt
      )
    } yield jwk
  }

  private val SUPPORTED_CURVES = Set(Curve.P_256, Curve.P_384, Curve.P_521)

  private def ensurePublicCoordinatesOnCurve(curve: Curve, x: Base64String, y: Base64String) = {
    require(!SUPPORTED_CURVES.contains(curve), "Unknown / unsupported curve: " + curve)
    val result = for {
      dx <- x.decodeToBigInt
      dy <- y.decodeToBigInt
    } yield
      ECChecks.isPointOnCurve(
        dx,
        dy,
        curve.toECParameterSpec.getOrElse(throw new IllegalArgumentException("Unknown curve instance."))
      )

    if (!result.isLeft)
      throw new IllegalArgumentException(
        "Invalid EC JWK: The 'x' and 'y' public coordinates are not on the " + curve + " curve"
      )
  }

  private def encodeCoordinate(fieldSize: Int, coordinate: BigInt): Either[Base64EncodeError, Base64String] = {
    val base64StringFactory = Base64StringFactory(urlSafe = true, isNoPadding = true)
    val notPadded           = BigIntUtils.toBytesUnsigned(coordinate)
    val bytesToOutput       = (fieldSize + 7) / 8
    if (notPadded.length >= bytesToOutput) { // Greater-than check to prevent exception on malformed
      // key below
      base64StringFactory.encode(notPadded)
    } else {
      val padded = new Array[Byte](bytesToOutput)
      System.arraycopy(notPadded, 0, padded, bytesToOutput - notPadded.length, notPadded.length)
      base64StringFactory.encode(padded)
    }
  }

}

class ECJWK private[jwk] (val curve: Curve,
                          val x: Base64String,
                          val y: Base64String,
                          publicKeyUseType: Option[PublicKeyUseType] = None,
                          keyOperations: KeyOperations = KeyOperations.empty,
                          algorithmType: Option[JWSAlgorithmType] = None,
                          keyId: Option[KeyId] = None,
                          x509Url: Option[URI] = None,
                          x509CertificateSHA1Thumbprint: Option[Base64String] = None,
                          x509CertificateSHA256Thumbprint: Option[Base64String] = None,
                          x509CertificateChain: List[Base64String] = List.empty,
                          val d: Option[Base64String] = None,
                          val privateKey: Option[PrivateKey] = None,
                          expireAt: Option[ZonedDateTime] = None,
                          keyStore: Option[KeyStore] = None)
    extends JWK(
      KeyType.EC,
      publicKeyUseType,
      keyOperations,
      algorithmType,
      keyId,
      x509Url,
      x509CertificateSHA256Thumbprint,
      x509CertificateSHA1Thumbprint,
      x509CertificateChain,
      expireAt,
      keyStore
    )
    with AssymetricJWK
    with CurveBasedJWK {

  require(x.urlSafe)
  require(y.urlSafe)
  require(x509CertificateSHA1Thumbprint.fold(true)(_.urlSafe))
  require(x509CertificateSHA256Thumbprint.fold(true)(_.urlSafe))
  require(d.fold(true)(_.urlSafe))

  ECJWK.ensurePublicCoordinatesOnCurve(curve, x, y)

  def toECPublicKey(provider: Option[Provider] = None): Either[PublicKeyCreationError, ECPublicKey] = {
    curve.toECParameterSpec
      .map { spec =>
        for {
          dx <- x.decodeToBigInt.leftMap(error => PublicKeyCreationError(error.message))
          dy <- y.decodeToBigInt.leftMap(error => PublicKeyCreationError(error.message))
          publicKeySpec = new ECPublicKeySpec(new ECPoint(dx.bigInteger, dy.bigInteger), spec)
          result <- try {
            val keyFactory = provider.map(p => KeyFactory.getInstance("EC", p)).getOrElse(KeyFactory.getInstance("EC"))
            Right(keyFactory.generatePublic(publicKeySpec).asInstanceOf[ECPublicKey])
          } catch {
            case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
              Left(PublicKeyCreationError(e.getMessage))
          }
        } yield result
      }
      .getOrElse(Left(PublicKeyCreationError("Couldn't get EC parameter spec for curve " + curve)))
  }

  def toECPrivateKey(provider: Option[Provider] = None): Either[PrivateKeyCreationError, Option[ECPrivateKey]] = {
    d match {
      case None =>
        Right(None)
      case Some(_d) =>
        curve.toECParameterSpec
          .map { spec =>
            for {
              dx <- _d.decodeToBigInt.leftMap(error => PrivateKeyCreationError(error.message))
              privateKeySpec = new ECPrivateKeySpec(dx.bigInteger, spec)
              result <- try {
                val keyFactory =
                  provider.map(p => KeyFactory.getInstance("EC", p)).getOrElse(KeyFactory.getInstance("EC"))
                Right(Some(keyFactory.generatePrivate(privateKeySpec).asInstanceOf[ECPrivateKey]))
              } catch {
                case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
                  Left(PrivateKeyCreationError(e.getMessage))
              }
            } yield result
          }
          .getOrElse(Left(PrivateKeyCreationError("Couldn't get EC parameter spec for curve " + curve)))
    }
  }

  def toKeyPair(provider: Option[Provider]): Either[KeyCreationError, Option[KeyPair]] = {
    privateKey
      .map { privKey =>
        toECPublicKey(provider).map { pubKey =>
          Option(new KeyPair(pubKey, privKey))
        }
      }
      .getOrElse {
        for {
          pubKey     <- toECPublicKey(provider)
          privKeyOpt <- toECPrivateKey(provider)
        } yield
          privKeyOpt.map { pko =>
            new KeyPair(pubKey, pko)
          }
      }
  }

  override def getRequiredParams: Map[String, Any] = Map(
    "crv" -> curve.name,
    "kty" -> keyType.entryName,
    "x"   -> x.asString,
    "y"   -> y.asString
  )

  override def isPrivate: Boolean = d.nonEmpty || privateKey.nonEmpty

  override def toPublicJWK: JWK = new ECJWK(
    curve,
    x,
    y,
    publicKeyUseType,
    keyOperations,
    algorithmType,
    keyId,
    x509Url,
    x509CertificateSHA1Thumbprint,
    x509CertificateSHA256Thumbprint,
    x509CertificateChain
  )

  override def size: Either[JWKError.JOSEError, Int] = {
    curve.toECParameterSpec
      .map { spec =>
        Right(spec.getCurve.getField.getFieldSize)
      }
      .getOrElse(Left(JOSEError("Couldn't determine field size for curve " + curve.name)))
  }

  override def toPublicKey: Either[PublicKeyCreationError, PublicKey] = toECPublicKey(None)

  override def toPrivateKey: Either[PrivateKeyCreationError, PrivateKey] = {
    for {
      prv <- toECPrivateKey()
      result <- prv.map(Right(_)).getOrElse {
        privateKey
          .map(Right(_))
          .getOrElse(Left(PrivateKeyCreationError("Illegal Argument: privateKey is not found")))
      }
    } yield result
  }

  override def toKeyPair: Either[KeyCreationError, KeyPair] = toKeyPair(None).map(_.get)

  override def compare(that: JWK): Int = super.compareTo(that)

  override def canEqual(other: Any): Boolean = other.isInstanceOf[ECJWK]

  override def equals(other: Any): Boolean = other match {
    case that: ECJWK =>
      super.equals(that) &&
      (that canEqual this) &&
      curve == that.curve &&
      x == that.x &&
      y == that.y &&
      d == that.d &&
      privateKey == that.privateKey
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(curve, x, y, d, privateKey)
    state.map(_.hashCode()).foldLeft(super.hashCode())((a, b) => 31 * a + b)
  }

  override def toString =
    (
      Seq(
        curve,
        x,
        y,
        keyType,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA256Thumbprint,
        x509CertificateSHA1Thumbprint,
        x509CertificateChain,
        d,
        privateKey
      )
    ).mkString("ECJWK(", ",", ")")

}

trait ECJWKJsonImplicits extends JsonImplicits {
  import io.circe.syntax._

  implicit val CurveJsonEncoder: Encoder[Curve] = Encoder[String].contramap(_.name)

  implicit val CurveJsonDecoder: Decoder[Curve] = Decoder[String].map(v => Curve.withName(v).get)

  implicit val ECJWKJsonEncoder: Encoder[ECJWK] = Encoder.instance { v =>
    Json.obj(
      "kty"     -> v.keyType.asJson,
      "use"     -> v.publicKeyUseType.asJson,
      "ops"     -> v.keyOperations.asJson,
      "alg"     -> v.algorithmType.asJson,
      "kid"     -> v.keyId.asJson,
      "x5u"     -> v.x509Url.asJson,
      "x5t"     -> v.x509CertificateSHA1Thumbprint.asJson,
      "x5t#256" -> v.x509CertificateSHA256Thumbprint.asJson,
      "x5c"     -> v.x509CertificateChain.asJson,
      "crv"     -> v.curve.asJson,
      "x"       -> v.x.asJson,
      "y"       -> v.y.asJson
    )
  }

  implicit val ECJWKJsonDecoder: Decoder[ECJWK] = Decoder.instance { hcursor =>
    for {
      _ <- hcursor.get[KeyType]("kty").flatMap { v =>
        if (v == KeyType.RSA) Right(v) else Left(DecodingFailure("Invalid key type", hcursor.history))
      }
      use    <- hcursor.get[Option[PublicKeyUseType]]("use")
      ops    <- hcursor.getOrElse[KeyOperations]("ops")(KeyOperations.empty)
      alg    <- hcursor.getOrElse[Option[JWSAlgorithmType]]("alg")(None)
      kid    <- hcursor.getOrElse[Option[KeyId]]("kid")(None)
      x5u    <- hcursor.getOrElse[Option[URI]]("k5u")(None)
      k5t    <- hcursor.getOrElse[Option[Base64String]]("k5t")(None)
      k5t256 <- hcursor.getOrElse[Option[Base64String]]("k5t#256")(None)
      k5c    <- hcursor.getOrElse[List[Base64String]]("k5c")(List.empty)
      crv    <- hcursor.get[Curve]("crv")
      x      <- hcursor.get[Base64String]("x")
      y      <- hcursor.get[Base64String]("y")
    } yield
      new ECJWK(
        crv,
        x,
        y,
        use,
        ops,
        alg,
        kid,
        x5u,
        k5t,
        k5t256,
        k5c
      )
  }

}
