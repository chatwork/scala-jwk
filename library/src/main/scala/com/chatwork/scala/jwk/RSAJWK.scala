package com.chatwork.scala.jwk

import cats.data.NonEmptyList

import java.net.URI
import java.security._
import java.security.interfaces.{ RSAPrivateCrtKey, RSAPrivateKey, RSAPublicKey }
import java.security.spec._
import java.time.ZonedDateTime
import com.chatwork.scala.jwk.JWKError._
import com.github.j5ik2o.base64scala.{ Base64String, Base64StringFactory }
import io.circe._
import io.circe.syntax._

object RSAJWK extends RSAJWKJsonImplicits {

  def parseFromJson(json: Json): Either[JWKCreationError, RSAJWK] =
    json.as[RSAJWK].left.map(error => JWKCreationError(error.getMessage, None))

  def parseFromText(jsonText: String): Either[JWKCreationError, RSAJWK] = {
    parser
      .parse(jsonText) match {
      case Left(error) =>
        Left(JWKCreationError(error.getMessage()))
      case Right(json) =>
        parseFromJson(json)
    }
  }

  private val base64StringFactory = Base64StringFactory(urlSafe = true, isNoPadding = true)

  def apply(
      n: Base64String,
      e: Base64String,
      publicKeyUse: Option[PublicKeyUseType] = None,
      keyOperations: KeyOperations = KeyOperations.empty,
      algorithmType: Option[JWSAlgorithmType] = None,
      keyId: Option[KeyId] = None,
      x509Url: Option[URI] = None,
      x509CertificateSHA1Thumbprint: Option[Base64String] = None,
      x509CertificateSHA256Thumbprint: Option[Base64String] = None,
      x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
      d: Option[Base64String] = None,
      p: Option[Base64String] = None,
      q: Option[Base64String] = None,
      dp: Option[Base64String] = None,
      dq: Option[Base64String] = None,
      qi: Option[Base64String] = None,
      oth: Seq[OtherPrimesInfo] = Seq.empty,
      privateKey: Option[PrivateKey] = None,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
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

  def fromRSAPublicKey(
      rsaPublicKey: RSAPublicKey,
      publicKeyUseType: Option[PublicKeyUseType] = None,
      keyOperations: KeyOperations = KeyOperations.empty,
      algorithmType: Option[JWSAlgorithmType] = None,
      keyId: Option[KeyId] = None,
      x509Url: Option[URI] = None,
      x509CertificateSHA1Thumbprint: Option[Base64String] = None,
      x509CertificateSHA256Thumbprint: Option[Base64String] = None,
      x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64StringFactory
        .encode(rsaPublicKey.getModulus)
        .left
        .map(error => JWKCreationError(error.message))
      e <- base64StringFactory
        .encode(rsaPublicKey.getPublicExponent)
        .left
        .map(error => JWKCreationError(error.message))
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

  def fromKeyPair(
      rsaPublicKey: RSAPublicKey,
      rsaPrivateKey: RSAPrivateKey,
      publicKeyUseType: Option[PublicKeyUseType] = None,
      keyOperations: KeyOperations = KeyOperations.empty,
      keyId: Option[KeyId] = None,
      algorithmType: Option[JWSAlgorithmType] = None,
      x509Url: Option[URI] = None,
      x509CertificateSHA1Thumbprint: Option[Base64String] = None,
      x509CertificateSHA256Thumbprint: Option[Base64String] = None,
      x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
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
      x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64StringFactory
        .encode(rsaPublicKey.getModulus)
        .left
        .map(error => JWKCreationError(error.message, None))
      e <- base64StringFactory
        .encode(rsaPublicKey.getPublicExponent)
        .left
        .map(error => JWKCreationError(error.message, None))
      d <- base64StringFactory
        .encode(rsaPrivateKey.getPrivateExponent)
        .left
        .map(error => JWKCreationError(error.message, None))
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
      x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
      expireAt: Option[ZonedDateTime] = None
  ): Either[JWKCreationError, RSAJWK] = {
    for {
      n <- base64StringFactory
        .encode(rsaPublicKey.getModulus)
        .left
        .map(error => JWKCreationError(error.message))
      e <- base64StringFactory
        .encode(rsaPublicKey.getPublicExponent)
        .left
        .map(error => JWKCreationError(error.message))
      d <- base64StringFactory
        .encode(rsaPrivateKey.getPrivateExponent)
        .left
        .map(error => JWKCreationError(error.message))
      p <- base64StringFactory
        .encode(rsaPrivateKey.getPrimeP)
        .left
        .map(error => JWKCreationError(error.message))
      q <- base64StringFactory
        .encode(rsaPrivateKey.getPrimeQ)
        .left
        .map(error => JWKCreationError(error.message))
      dp <- base64StringFactory
        .encode(rsaPrivateKey.getPrimeExponentP)
        .left
        .map(error => JWKCreationError(error.message))
      dq <- base64StringFactory
        .encode(rsaPrivateKey.getPrimeExponentQ)
        .left
        .map(error => JWKCreationError(error.message))
      qi <- base64StringFactory
        .encode(rsaPrivateKey.getCrtCoefficient)
        .left
        .map(error => JWKCreationError(error.message))
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
    x509CertificateChain: Option[NonEmptyList[Base64String]] = None,
    d: Option[Base64String] = None,
    p: Option[Base64String] = None,
    q: Option[Base64String] = None,
    dp: Option[Base64String] = None,
    dq: Option[Base64String] = None,
    qi: Option[Base64String] = None,
    oth: Seq[OtherPrimesInfo] = Seq.empty,
    privateKey: Option[PrivateKey] = None,
    expireAt: Option[ZonedDateTime] = None,
    keyStore: Option[KeyStore] = None
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
      expireAt,
      keyStore
    )
    with AssymetricJWK {

  require(n.urlSafe)
  require(e.urlSafe)
  require(x509CertificateSHA1Thumbprint.fold(true)(_.urlSafe))
  require(x509CertificateSHA256Thumbprint.fold(true)(_.urlSafe))
  require(d.fold(true)(_.urlSafe))
  require(p.fold(true)(_.urlSafe))
  require(q.fold(true)(_.urlSafe))
  require(dp.fold(true)(_.urlSafe))
  require(dq.fold(true)(_.urlSafe))
  require(qi.fold(true)(_.urlSafe))

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
  val otherPrimes: Seq[OtherPrimesInfo]             = oth

  override def getRequiredParams: Map[String, Any] =
    Map("e" -> e.toString, "kty" -> keyType.entryName, "n" -> n.toString)

  def toRSAPrivateKey: Either[PrivateKeyCreationError, Option[RSAPrivateKey]] = {
    privateExponent
      .map { _d =>
        val privateKeySpecEither = for {
          _modulus <- modulus.decodeToBigInt.left.map(err =>
            PrivateKeyCreationError(s"KeySpec creation failed.(${err.message})")
          )
          _privateExponent <- _d.decodeToBigInt.left.map(err =>
            PrivateKeyCreationError(s"KeySpec creation failed.(${err.message})")
          )
          privateKeySpec <- createRSAPrivateKeySpec(_modulus, _privateExponent)
        } yield privateKeySpec
        privateKeySpecEither.flatMap { spec =>
          try {
            val factory = KeyFactory.getInstance("RSA")
            Right(Some(factory.generatePrivate(spec).asInstanceOf[RSAPrivateKey]))
          } catch {
            case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
              Left(PrivateKeyCreationError(e.getMessage))
          }
        }
      }
      .getOrElse(Right(None))
  }

  private def createRSAPrivateKeySpec(
      _modulus: BigInt,
      _privateExponent: BigInt
  ): Either[PrivateKeyCreationError, RSAPrivateKeySpec] = {
    p.map { _p =>
      for {
        _publicExponent <- e.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message))
        _primeP         <- _p.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message))
        _primeQ <- q
          .map(_.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message)))
          .getOrElse(Left(PrivateKeyCreationError("primeQ is not found.")))
        _primeExponentP <- dp
          .map(_.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message)))
          .getOrElse(Left(PrivateKeyCreationError("primeExponentP is not found.")))
        _primeExponentQ <- dq
          .map(_.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message)))
          .getOrElse(Left(PrivateKeyCreationError("primeExponentQ is not found.")))
        _crtCoefficient <- qi
          .map(_.decodeToBigInt.left.map(e => PrivateKeyCreationError(e.message)))
          .getOrElse(Left(PrivateKeyCreationError("crtCoefficient is not found.")))
        spec <- createInternalPrivateKeySpec(
          _modulus,
          _publicExponent,
          _privateExponent,
          _primeP,
          _primeQ,
          _primeExponentP,
          _primeExponentQ,
          _crtCoefficient
        )
      } yield spec
    }.getOrElse {
        Right[PrivateKeyCreationError, RSAPrivateKeySpec](
          new RSAPrivateKeySpec(_modulus.bigInteger, _privateExponent.bigInteger)
        )
      }
  }

  private def createInternalPrivateKeySpec(
      _modulus: BigInt,
      _publicExponent: BigInt,
      _privateExponent: BigInt,
      _primeP: BigInt,
      _primeQ: BigInt,
      _primeExponentP: BigInt,
      _primeExponentQ: BigInt,
      _crtCoefficient: BigInt
  ): Either[PrivateKeyCreationError, RSAPrivateKeySpec] = {
    otherPrimes
      .foldLeft[Either[PrivateKeyCreationError, Seq[RSAOtherPrimeInfo]]](
        Right(Seq.empty)
      ) { (r, otherPrimesInfo) =>
        val e = for {
          otherPrime <- otherPrimesInfo.primeFactor.decodeToBigInt
            .map(_.bigInteger)
            .left
            .map(e => PrivateKeyCreationError(e.message))
          otherPrimeExponent <- otherPrimesInfo.factorCRTExponent.decodeToBigInt
            .map(_.bigInteger)
            .left
            .map(e => PrivateKeyCreationError(e.message))
          otherCrtCoefficient <- otherPrimesInfo.factorCRTCoefficient.decodeToBigInt
            .map(_.bigInteger)
            .left
            .map(e => PrivateKeyCreationError(e.message))
        } yield new RSAOtherPrimeInfo(otherPrime, otherPrimeExponent, otherCrtCoefficient)
        for {
          result <- r
          _e     <- e
        } yield result :+ _e
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

  def toRSAPublicKey: Either[PublicKeyCreationError, RSAPublicKey] = {
    val specEither = (for {
      modulus  <- n.decodeToBigInt
      exponent <- e.decodeToBigInt
      spec     <- Right(new RSAPublicKeySpec(modulus.bigInteger, exponent.bigInteger))
    } yield spec).left.map(e => PublicKeyCreationError(s"KeySpec creation failed.(${e.message})"))
    specEither.flatMap { spec =>
      try {
        val factory = KeyFactory.getInstance("RSA")
        Right(factory.generatePublic(spec).asInstanceOf[RSAPublicKey])
      } catch {
        case e @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
          Left(PublicKeyCreationError(e.getMessage))
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

  override def size: Either[JOSEError, Int] =
    for {
      _n <- n.decode.left.map(err => JOSEError(err.message))
      r  <- ByteUtils.safeBitLength(_n)
    } yield r

  override def toPublicKey: Either[PublicKeyCreationError, PublicKey] = toRSAPublicKey

  override def toPrivateKey: Either[PrivateKeyCreationError, PrivateKey] = {
    for {
      prv <- toRSAPrivateKey
      result <- prv.map(Right(_)).getOrElse {
        privateKey
          .map(Right(_))
          .getOrElse(Left(PrivateKeyCreationError("Illegal Argument: privateKey is not found")))
      }
    } yield result
  }

  override def toKeyPair: Either[KeyCreationError, KeyPair] = {
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
      Seq(
        keyType,
        publicKeyUseType,
        keyOperations,
        algorithmType,
        keyId,
        x509Url,
        x509CertificateSHA256Thumbprint,
        x509CertificateSHA1Thumbprint,
        x509CertificateChain
      ) ++
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
    JWKPrinter.noSpaces.print(this.asJson)
  }

  override def compare(that: JWK): Int = super.compareTo(that)
}

trait RSAJWKJsonImplicits extends JsonImplicits {

  import io.circe.syntax._

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
      k5c    <- hcursor.getOrElse[Option[NonEmptyList[Base64String]]]("k5c")(None)
      n      <- hcursor.get[Base64String]("n")
      e      <- hcursor.get[Base64String]("e")
      d      <- hcursor.getOrElse[Option[Base64String]]("d")(None)
      p      <- hcursor.getOrElse[Option[Base64String]]("p")(None)
      q      <- hcursor.getOrElse[Option[Base64String]]("q")(None)
      dp     <- hcursor.getOrElse[Option[Base64String]]("dp")(None)
      dq     <- hcursor.getOrElse[Option[Base64String]]("dq")(None)
      qi     <- hcursor.getOrElse[Option[Base64String]]("qi")(None)
    } yield new RSAJWK(
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
