package com.chatwork.scala.jwk

import java.net.URI
import java.security.KeyStore
import java.time.ZonedDateTime

import com.chatwork.scala.jwk.JWKError.{ JOSEError, JWKThumbprintError }
import com.github.j5ik2o.base64scala.Base64String
import io.circe.{ Decoder, Encoder }

abstract class JWK(
    val keyType: KeyType,
    val publicKeyUseType: Option[PublicKeyUseType],
    val keyOperations: KeyOperations,
    val algorithmType: Option[JWSAlgorithmType],
    val keyId: Option[KeyId],
    val x509Url: Option[URI],
    val x509CertificateSHA256Thumbprint: Option[Base64String],
    val x509CertificateSHA1Thumbprint: Option[Base64String],
    val x509CertificateChain: List[Base64String],
    val expireAt: Option[ZonedDateTime],
    val keyStore: Option[KeyStore]
) extends Ordered[JWK] {

  require(x509CertificateSHA256Thumbprint.fold(true)(_.urlSafe))
  require(x509CertificateSHA1Thumbprint.fold(true)(_.urlSafe))
  require(if (x509CertificateChain.isEmpty) true else x509CertificateChain.forall(_.urlSafe))

  require(
    publicKeyUseType.fold(true) { pku => KeyUseAndOpsConsistency.areConsistent(pku, keyOperations) },
    "The key use \"use\" and key options \"key_opts\" parameters are not consistent, " +
    "see RFC 7517, section 4.3"
  )

  def getRequiredParams: Map[String, Any]

  def computeThumbprint: Either[JWKThumbprintError, Base64String] = computeThumbprint("SHA-256")

  def computeThumbprint(hashAlg: String): Either[JWKThumbprintError, Base64String] =
    JWKThumbprint.computeFromJWK(this, hashAlg)

  def isPrivate: Boolean

  def toPublicJWK: JWK

  def size: Either[JOSEError, Int]

  import scala.math.Ordered._

  override def compareTo(that: JWK): Int = {
    (keyId.map(_.value), expireAt.map(_.toInstant.toEpochMilli)) compare (that.keyId.map(_.value), that.expireAt.map(
      _.toInstant.toEpochMilli
    ))
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[JWK]

  override def equals(other: Any): Boolean = other match {
    case that: JWK =>
      (that canEqual this) &&
        keyType == that.keyType &&
        publicKeyUseType == that.publicKeyUseType &&
        keyOperations == that.keyOperations &&
        algorithmType == that.algorithmType &&
        keyId == that.keyId &&
        x509Url == that.x509Url &&
        x509CertificateSHA256Thumbprint == that.x509CertificateSHA256Thumbprint &&
        x509CertificateSHA1Thumbprint == that.x509CertificateSHA1Thumbprint &&
        x509CertificateChain == that.x509CertificateChain
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(
      keyType,
      publicKeyUseType,
      keyOperations,
      algorithmType,
      keyId,
      x509Url,
      x509CertificateSHA256Thumbprint,
      x509CertificateSHA1Thumbprint,
      x509CertificateChain
    )
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

trait JWKJsonImplicits extends RSAJWKJsonImplicits with ECJWKJsonImplicits {

  import io.circe.syntax._

  implicit val JWKJsonEncoder: Encoder[JWK] = Encoder.instance {
    case v: RSAJWK =>
      v.asJson
    case v: ECJWK =>
      v.asJson
    case _ =>
      throw new AssertionError("Unsupported Other KeyType")
  }

  implicit val JWKJsonDecoder: Decoder[JWK] = Decoder.instance { hcursor =>
    hcursor.get[KeyType]("kty").flatMap {
      case KeyType.RSA =>
        RSAJWKJsonDecoder(hcursor)
      case KeyType.EC =>
        ECJWKJsonDecoder(hcursor)
      case _ =>
        throw new AssertionError("Unsupported Other KeyType")
    }
  }

}
