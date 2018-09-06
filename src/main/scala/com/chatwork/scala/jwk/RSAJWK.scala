package com.chatwork.scala.jwk

import java.net.URI
import java.security._
import java.security.interfaces.{RSAPrivateCrtKey, RSAPrivateKey, RSAPublicKey}
import java.security.spec._
import java.time.ZonedDateTime

import cats.implicits._

import com.chatwork.scala.jwk.JWKError.{JWKCreationError, RSAPrivateKeyCreationError, RSAPublicKeyCreationError}
import com.github.j5ik2o.base64scala.{Base64String, Base64StringFactory}
import io.circe._
import io.circe.syntax._

object RSAJWK extends RSAJWKJsonImplicits {

  def parseFromJson(json: Json): Either[JWKCreationError, RSAJWK] =
    json.as[RSAJWK].leftMap(error => JWKCreationError(error.getMessage, None))

  def parseFromText(jsonText: String): Either[JWKCreationError, RSAJWK] = {
    parser
      .parse(jsonText) match {
      case Left(error) =>
        Left(JWKCreationError(error.getMessage(), None))
      case Right(json) =>
        parseFromJson(json)
    }
  }

  val base64Factory = Base64StringFactory(urlSafe = true, isNoPadding = true)

  def apply(n: Base64String,
            e: Base64String,
            publicKeyUse: Option[PublicKeyUseType] = None,
            keyOperations: KeyOperations = KeyOperations.empty,
            algorithmType: Option[JWSAlgorithmType] = None,
            keyId: Option[KeyId] = None,
            x509Url: Option[URI] = None,
            x509CertificateSHA1Thumbprint: Option[Base64String] = None,
            x509CertificateSHA256Thumbprint: Option[Base64String] = None,
            x509CertificateChain: List[Base64String] = List.empty,
            d: Option[Base64String] = None,
            p: Option[Base64String] = None,
            q: Option[Base64String] = None,
            dp: Option[Base64String] = None,
            dq: Option[Base64String] = None,
            qi: Option[Base64String] = None,
            oth: Seq[OtherPrimesInfo] = Seq.empty,
            privateKey: Option[PrivateKey] = None,
            expireAt: Option[ZonedDateTime] = None): Either[JWKCreationError, RSAJWK] = {
    try {
      Right(
        new RSAJWK(
          n,
          e,
          publicKeyUse,
          keyOperations,
          algorithmType,
          keyId,
          x509Url,
          x509CertificateSHA1Thumbprint,
          x509CertificateSHA256Thumbprint,
          x509CertificateChain,
          d,
          p,
          q,
          dp,
          dq,
          qi,
          oth,
          privateKey,
          expireAt
        )
      )
    } catch {
      case ex: IllegalArgumentException =>
        Left(JWKCreationError(ex.getMessage))
    }
  }

  def fromRSAPublicKey(rsaPublicKey: RSAPublicKey,
                       publicKeyUseType: Option[PublicKeyUseType] = None,
                       keyOperations: KeyOperations = KeyOperations.empty,
                       keyId: Option[KeyId] = None,
                       algorithmType: Option[JWSAlgorithmType] = None,
                       x509Url: Option[URI] = None,
                       x509CertificateSHA1Thumbprint: Option[Base64String] = None,
                       x509CertificateSHA256Thumbprint: Option[Base64String] = None,
                       x509CertificateChain: List[Base64String] = List.empty,
                       expireAt: Option[ZonedDateTime] = None): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64Factory
        .encode(rsaPublicKey.getModulus)
        .leftMap(error => JWKCreationError(error.message))
      e <- base64Factory
        .encode(rsaPublicKey.getPublicExponent)
        .leftMap(error => JWKCreationError(error.message))
      result <- apply(
        n,
        e,
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
    } yield result
  }

  def fromKeyPair(rsaPublicKey: RSAPublicKey,
                  rsaPrivateKey: RSAPrivateKey,
                  publicKeyUseType: Option[PublicKeyUseType] = None,
                  keyOperations: KeyOperations = KeyOperations.empty,
                  keyId: Option[KeyId] = None,
                  algorithmType: Option[JWSAlgorithmType] = None,
                  x509Url: Option[URI] = None,
                  x509CertificateSHA1Thumbprint: Option[Base64String] = None,
                  x509CertificateSHA256Thumbprint: Option[Base64String] = None,
                  x509CertificateChain: List[Base64String] = List.empty,
                  expireAt: Option[ZonedDateTime] = None): Either[JWKCreationError, RSAJWK] = {
    rsaPrivateKey match {
      case pv: RSAPrivateCrtKey =>
        fromPublicKeyWithPrivateCrtKey(
          rsaPublicKey,
          pv,
          publicKeyUseType,
          keyOperations,
          keyId,
          algorithmType,
          x509Url,
          x509CertificateSHA1Thumbprint,
          x509CertificateSHA256Thumbprint,
          x509CertificateChain,
          expireAt = expireAt
        )
      case _ =>
        fromPublicKeyWithPrivateKey(
          rsaPublicKey,
          rsaPrivateKey,
          publicKeyUseType,
          keyOperations,
          keyId,
          algorithmType,
          x509Url,
          x509CertificateSHA1Thumbprint,
          x509CertificateSHA256Thumbprint,
          x509CertificateChain,
          expireAt = expireAt
        )
    }
  }


  private[jwk] def fromPublicKeyWithPrivateKey(
      rsaPublicKey: RSAPublicKey,
      rsaPrivateKey: RSAPrivateKey,
      publicKeyUseType: Option[PublicKeyUseType] = None,
      keyOperations: KeyOperations = KeyOperations.empty,
      keyId: Option[KeyId] = None,
      algorithmType: Option[JWSAlgorithmType] = None,
      x509Url: Option[URI] = None,
      x509CertificateSHA1Thumbprint: Option[Base64String] = None,
      x509CertificateSHA256Thumbprint: Option[Base64String] = None,
      x509CertificateChain: List[Base64String] = List.empty,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64Factory.encode(rsaPublicKey.getModulus)
        .leftMap(error => JWKCreationError(error.message, None))
      e <- base64Factory.encode(rsaPublicKey.getPublicExponent)
        .leftMap(error => JWKCreationError(error.message, None))
      d <- base64Factory
        .encode(rsaPrivateKey.getPrivateExponent)
        .leftMap(error => JWKCreationError(error.message, None))
      result <- apply(
        n,
        e,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA1Thumbprint,
        x509CertificateSHA256Thumbprint,
        x509CertificateChain,
        Some(d),
        expireAt = expireAt
      )
    } yield result
  }

  private[jwk] def fromPublicKeyWithPrivateCrtKey(
      rsaPublicKey: RSAPublicKey,
      rsaPrivateKey: RSAPrivateCrtKey,
      publicKeyUseType: Option[PublicKeyUseType] = None,
      keyOperations: KeyOperations = KeyOperations.empty,
      keyId: Option[KeyId] = None,
      algorithmType: Option[JWSAlgorithmType] = None,
      x509Url: Option[URI] = None,
      x509CertificateSHA1Thumbprint: Option[Base64String] = None,
      x509CertificateSHA256Thumbprint: Option[Base64String] = None,
      x509CertificateChain: List[Base64String] = List.empty,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64Factory
        .encode(rsaPublicKey.getModulus)
        .leftMap(error => JWKCreationError(error.message))
      e <- base64Factory
        .encode(rsaPublicKey.getPublicExponent)
        .leftMap(error => JWKCreationError(error.message))
      d <- base64Factory
        .encode(rsaPrivateKey.getPrivateExponent)
        .leftMap(error => JWKCreationError(error.message))
      p <- base64Factory
        .encode(rsaPrivateKey.getPrimeP)
        .leftMap(error => JWKCreationError(error.message))
      q <- base64Factory
        .encode(rsaPrivateKey.getPrimeQ)
        .leftMap(error => JWKCreationError(error.message))
      dp <- base64Factory
        .encode(rsaPrivateKey.getPrimeExponentP)
        .leftMap(error => JWKCreationError(error.message))
      dq <- base64Factory
        .encode(rsaPrivateKey.getPrimeExponentQ)
        .leftMap(error => JWKCreationError(error.message))
      qi <- base64Factory
        .encode(rsaPrivateKey.getCrtCoefficient)
        .leftMap(error => JWKCreationError(error.message))
      result <- apply(
        n,
        e,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA1Thumbprint,
        x509CertificateSHA256Thumbprint,
        x509CertificateChain,
        Some(d),
        Some(p),
        Some(q),
        Some(dp),
        Some(dq),
        Some(qi),
        expireAt = expireAt
      )
    } yield result
  }

}

class RSAJWK private[jwk] (
    n: Base64String,
    e: Base64String,
    publicKeyUseType: Option[PublicKeyUseType] = None,
    keyOperations: KeyOperations = KeyOperations.empty,
    algorithmType: Option[JWSAlgorithmType] = None,
    keyId: Option[KeyId] = None,
    x509Url: Option[URI] = None,
    x509CertificateSHA1Thumbprint: Option[Base64String] = None,
    x509CertificateSHA256Thumbprint: Option[Base64String] = None,
    x509CertificateChain: List[Base64String] = List.empty,
    d: Option[Base64String] = None,
    p: Option[Base64String] = None,
    q: Option[Base64String] = None,
    dp: Option[Base64String] = None,
    dq: Option[Base64String] = None,
    qi: Option[Base64String] = None,
    oth: Seq[OtherPrimesInfo] = Seq.empty,
    privateKey: Option[PrivateKey] = None,
    expireAt: Option[ZonedDateTime] = None
) extends JWK(
      KeyType.RSA,
      publicKeyUseType,
      keyOperations,
      algorithmType,
      keyId,
      x509Url,
      x509CertificateSHA256Thumbprint,
      x509CertificateSHA1Thumbprint,
      x509CertificateChain,
      expireAt
    )
    with AssymetricJWK {

  (p, q, dp, dq, qi) match {
    case (Some(_), Some(_), Some(_), Some(_), Some(_)) =>
    case (None, None, None, None, None)                =>
    case (None, _, _, _, _) =>
      throw new IllegalArgumentException(
        "Incomplete second private (CRT) representation: The first prime factor must not be empty"
      )
    case (_, None, _, _, _) =>
      throw new IllegalArgumentException(
        "Incomplete second private (CRT) representation: The second prime factor must not be empty"
      )
    case (_, _, None, _, _) =>
      throw new IllegalArgumentException(
        "Incomplete second private (CRT) representation: The first factor CRT exponent must not be empty"
      )
    case (_, _, _, None, _) =>
      throw new IllegalArgumentException(
        "Incomplete second private (CRT) representation: The second factor CRT exponent must not be empty"
      )
    case (_, _, _, _, None) =>
      throw new IllegalArgumentException(
        "Incomplete second private (CRT) representation: The first CRT coefficient must not be empty"
      )

  }

  val modulus: Base64String                         = n
  val publicExponent: Base64String                  = e
  val privateExponent: Option[Base64String]         = d
  val firstPrimeFactor: Option[Base64String]        = p
  val secondPrimeFactor: Option[Base64String]       = q
  val firstFactorCRTExponent: Option[Base64String]  = dp
  val secondFactorCRTExponent: Option[Base64String] = dq
  val firstCRTCoefficient: Option[Base64String]     = qi
  val otherPrimes: Seq[OtherPrimesInfo]                                  = oth

  override def getRequiredParams: Map[String, Any] =
    Map("e" -> e.toString, "kty" -> keyType.entryName, "n" -> n.toString)

  def toRSAPrivateKey: Either[RSAPrivateKeyCreationError, Option[RSAPrivateKey]] = {
    privateExponent
      .map { _d =>
        val privateKeySpecEither = (for {
          _modulus         <- modulus.decodeToBigInt
          _privateExponent <- _d.decodeToBigInt
          privateKeySpec   <- createRSAPrivateKeySpec(_modulus, _privateExponent)
        } yield privateKeySpec).leftMap(e => RSAPrivateKeyCreationError("KeySpec creation failed.", Some(e)))
        privateKeySpecEither.flatMap { spec =>
          try {
            val factory = KeyFactory.getInstance("RSA")
            Right(Some(factory.generatePrivate(spec).asInstanceOf[RSAPrivateKey]))
          } catch {
            case e: NoSuchAlgorithmException =>
              Left(RSAPrivateKeyCreationError(e.getMessage))
            case e: InvalidKeySpecException =>
              Left(RSAPrivateKeyCreationError(e.getMessage))
          }
        }
      }
      .getOrElse(Right(None))
  }

  private def createRSAPrivateKeySpec(_modulus: BigInt, _privateExponent: BigInt) = {
    p.map { _p =>
        for {
          _publicExponent <- e.decodeToBigInt
          _primeP         <- _p.decodeToBigInt
          _primeQ         <- q.map(_.decodeToBigInt).getOrElse(Left(RSAPrivateKeyCreationError("primeQ is not found.")))
          _primeExponentP <- dp
            .map(_.decodeToBigInt)
            .getOrElse(Left(RSAPrivateKeyCreationError("primeExponentP is not found.")))
          _primeExponentQ <- dq
            .map(_.decodeToBigInt)
            .getOrElse(Left(RSAPrivateKeyCreationError("primeExponentQ is not found.")))
          _crtCoefficient <- qi
            .map(_.decodeToBigInt)
            .getOrElse(Left(RSAPrivateKeyCreationError("crtCoefficient is not found.")))
          spec <- createInternalPrivateKeySpec(_modulus,
                                               _publicExponent,
                                               _privateExponent,
                                               _primeP,
                                               _primeQ,
                                               _primeExponentP,
                                               _primeExponentQ,
                                               _crtCoefficient)
        } yield spec
      }
      .getOrElse {
        Right(new RSAPrivateKeySpec(_modulus.bigInteger, _privateExponent.bigInteger))
      }
  }

  private def createInternalPrivateKeySpec(_modulus: BigInt,
                                           _publicExponent: BigInt,
                                           _privateExponent: BigInt,
                                           _primeP: BigInt,
                                           _primeQ: BigInt,
                                           _primeExponentP: BigInt,
                                           _primeExponentQ: BigInt,
                                           _crtCoefficient: BigInt) = {
    otherPrimes
      .foldLeft(Either.right[JWKError, Seq[RSAOtherPrimeInfo]](Seq.empty)) { (r, otherPrimesInfo) =>
        val e = for {
          otherPrime          <- otherPrimesInfo.primeFactor.decodeToBigInt.map(_.bigInteger)
          otherPrimeExponent  <- otherPrimesInfo.factorCRTExponent.decodeToBigInt.map(_.bigInteger)
          otherCrtCoefficient <- otherPrimesInfo.factorCRTCoefficient.decodeToBigInt.map(_.bigInteger)
        } yield new RSAOtherPrimeInfo(otherPrime, otherPrimeExponent, otherCrtCoefficient)
        (for { result <- r; _e <- e } yield result :+ _e).leftMap(err => JWKError(err.message))
      }
      .map { otherPrimeSeq =>
        if (otherPrimeSeq.nonEmpty)
          new RSAMultiPrimePrivateCrtKeySpec(
            _modulus.bigInteger,
            _publicExponent.bigInteger,
            _privateExponent.bigInteger,
            _primeP.bigInteger,
            _primeQ.bigInteger,
            _primeExponentP.bigInteger,
            _primeExponentQ.bigInteger,
            _crtCoefficient.bigInteger,
            otherPrimeSeq.toArray
          )
        else
          new RSAPrivateCrtKeySpec(
            _modulus.bigInteger,
            _publicExponent.bigInteger,
            _privateExponent.bigInteger,
            _primeP.bigInteger,
            _primeQ.bigInteger,
            _primeExponentP.bigInteger,
            _primeExponentQ.bigInteger,
            _crtCoefficient.bigInteger
          )
      }
  }

  def toRSAPublicKey: Either[RSAPublicKeyCreationError, RSAPublicKey] = {
    val specEither = (for {
      modulus  <- n.decodeToBigInt
      exponent <- e.decodeToBigInt
      spec     <- Right(new RSAPublicKeySpec(modulus.bigInteger, exponent.bigInteger))
    } yield spec).leftMap(e => RSAPublicKeyCreationError("KeySpec creation failed."))
    specEither.flatMap { spec =>
      try {
        val factory = KeyFactory.getInstance("RSA")
        Right(factory.generatePublic(spec).asInstanceOf[RSAPublicKey])
      } catch {
        case e: NoSuchAlgorithmException =>
          Left(RSAPublicKeyCreationError(e.getMessage))
        case e: InvalidKeySpecException =>
          Left(RSAPublicKeyCreationError(e.getMessage))
      }
    }
  }

  override def isPrivate: Boolean = d.nonEmpty || p.nonEmpty || privateKey.nonEmpty

  override def toPublicJWK: RSAJWK =
    new RSAJWK(
      modulus,
      publicExponent,
      publicKeyUseType,
      keyOperations,
      algorithmType,
      keyId,
      x509Url,
      x509CertificateSHA1Thumbprint,
      x509CertificateSHA256Thumbprint,
      x509CertificateChain
    )

  override def size: Either[JWKError, Int] =
    for {
      _n <- n.decode.leftMap(err => JWKError(err.message))
      r  <- ByteUtils.safeBitLength(_n)
    } yield r

  override def toPublicKey: Either[JWKError, PublicKey] = toRSAPublicKey

  override def toPrivateKey: Either[JWKError, PrivateKey] = {
    for {
      prv <- toRSAPrivateKey
      result <- prv.map(Right(_)).getOrElse {
        privateKey.map(Right(_)).getOrElse(Left(JWKError("Illegal Argument: privateKey is not found")))
      }
    } yield result
  }

  override def toKeyPair: Either[JWKError, KeyPair] = {
    for {
      publicKey  <- toRSAPublicKey
      privateKey <- toPrivateKey
    } yield new KeyPair(publicKey, privateKey)
  }

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RSAJWK]

  override def equals(other: Any): Boolean = other match {
    case that: RSAJWK =>
      super.equals(that) &&
      (that canEqual this) &&
      modulus == that.modulus &&
      publicExponent == that.publicExponent &&
      privateExponent == that.privateExponent &&
      firstPrimeFactor == that.firstPrimeFactor &&
      secondPrimeFactor == that.secondPrimeFactor &&
      firstFactorCRTExponent == that.firstFactorCRTExponent &&
      secondFactorCRTExponent == that.secondFactorCRTExponent &&
      firstCRTCoefficient == that.firstCRTCoefficient &&
      otherPrimes == that.otherPrimes
    case _ => false
  }

  override def hashCode(): Int = {
    val state: Seq[Any] = Seq(
      super.hashCode(),
      modulus,
      publicExponent,
      privateExponent,
      firstPrimeFactor,
      secondPrimeFactor,
      firstFactorCRTExponent,
      secondFactorCRTExponent,
      firstCRTCoefficient,
      otherPrimes
    )
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString =
    (Seq(modulus, publicExponent) ++
    Seq(keyType,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA256Thumbprint,
        x509CertificateSHA1Thumbprint,
        x509CertificateChain) ++
    Seq(
      privateExponent,
      firstPrimeFactor,
      secondPrimeFactor,
      firstFactorCRTExponent,
      secondFactorCRTExponent,
      firstCRTCoefficient,
      otherPrimes
    )).mkString("RSAJWK(", ",", ")")

  def toJsonString(implicit encoder: Encoder[RSAJWK]): String = {
    JWKPrinter.noSpaces.pretty(this.asJson)
  }

  override def compare(that: JWK): Int = super.compareTo(that)
}

trait RSAJWKJsonImplicits extends JsonImplicits {

  import io.circe.syntax._

  implicit val UriJsonEncoder: Encoder[URI] = Encoder[String].contramap(_.toString)

  implicit val UriJsonDecoder: Decoder[URI] = Decoder[String].map(URI.create)

  implicit val RSAJWKJsonEncoder: Encoder[RSAJWK] = Encoder.instance { v =>
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
      "n"       -> v.modulus.asJson,
      "e"       -> v.publicExponent.asJson,
      "d"       -> v.privateExponent.asJson,
      "p"       -> v.firstPrimeFactor.asJson,
      "q"       -> v.secondPrimeFactor.asJson,
      "dp"      -> v.firstFactorCRTExponent.asJson,
      "dq"      -> v.secondFactorCRTExponent.asJson,
      "qi"      -> v.firstCRTCoefficient.asJson
    )
  }

  implicit val RSAJWKJsonDecoder: Decoder[RSAJWK] = Decoder.instance { hcursor =>
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
      n      <- hcursor.get[Base64String]("n")
      e      <- hcursor.get[Base64String]("e")
      d      <- hcursor.getOrElse[Option[Base64String]]("d")(None)
      p      <- hcursor.getOrElse[Option[Base64String]]("p")(None)
      q      <- hcursor.getOrElse[Option[Base64String]]("q")(None)
      dp     <- hcursor.getOrElse[Option[Base64String]]("dp")(None)
      dq     <- hcursor.getOrElse[Option[Base64String]]("dq")(None)
      qi     <- hcursor.getOrElse[Option[Base64String]]("qi")(None)
    } yield
      new RSAJWK(
        n,
        e,
        use,
        ops,
        alg,
        kid,
        x5u,
        k5t,
        k5t256,
        k5c,
        d,
        p,
        q,
        dp,
        dq,
        qi
      )
  }

}
