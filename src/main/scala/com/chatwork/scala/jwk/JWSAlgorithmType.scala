package com.chatwork.scala.jwk

import io.circe.{ Decoder, Encoder }

import scala.collection.immutable

sealed abstract class JWSAlgorithmType(override val entryName: String, override val requirement: Requirement)
    extends AlgorithmType

object JWSAlgorithmType extends AlgorithmTypeFactory[JWSAlgorithmType] {

  def values: immutable.IndexedSeq[JWSAlgorithmType] =
    immutable.IndexedSeq(HS256, HS384, HS512, RS256, RS384, RS512, RSAOAEP, ES256, ES384, ES512, PS256, PS384, PS512)

  def withNameEither(name: String): Either[String, JWSAlgorithmType] =
    values.find(_.entryName == name).toRight(s"Unknown algorithm type $name")

  case object HS256 extends JWSAlgorithmType("HS256", Requirement.Required)
  case object HS384 extends JWSAlgorithmType("HS384", Requirement.Optional)
  case object HS512 extends JWSAlgorithmType("HS512", Requirement.Optional)

  case object RS256   extends JWSAlgorithmType("RS256", Requirement.Recommended)
  case object RS384   extends JWSAlgorithmType("RS384", Requirement.Optional)
  case object RS512   extends JWSAlgorithmType("RS512", Requirement.Optional)
  case object RSAOAEP extends JWSAlgorithmType("RSA-OAEP", Requirement.Optional)

  case object ES256 extends JWSAlgorithmType("ES256", Requirement.Recommended)
  case object ES384 extends JWSAlgorithmType("ES384", Requirement.Optional)
  case object ES512 extends JWSAlgorithmType("ES512", Requirement.Optional)

  case object PS256 extends JWSAlgorithmType("PS256", Requirement.Optional)
  case object PS384 extends JWSAlgorithmType("PS384", Requirement.Optional)
  case object PS512 extends JWSAlgorithmType("PS512", Requirement.Optional)

  sealed trait AlgorithmFamily {
    val entryName: String
    val values: Set[JWSAlgorithmType]
  }

  object AlgorithmFamily {
    def values: immutable.IndexedSeq[AlgorithmFamily] = immutable.IndexedSeq(HMacSHA, RSA, EC)
    case object HMacSHA extends AlgorithmFamily {
      override val entryName: String             = "HMacSHA"
      override val values: Set[JWSAlgorithmType] = Set(HS256, HS384, HS512)
    }
    case object RSA extends AlgorithmFamily {
      override val entryName: String             = "RSA"
      override val values: Set[JWSAlgorithmType] = Set(RS256, RS384, RS512, PS256, PS384, PS512, RSAOAEP)
    }
    case object EC extends AlgorithmFamily {
      override val entryName: String             = "EC"
      override val values: Set[JWSAlgorithmType] = Set(ES256, ES384, ES512)
    }
  }
}

trait JWSAlgorithmTypeJsonImplicits {

  implicit val JWSAlgorithmTypeJsonEncoder: Encoder[JWSAlgorithmType] = Encoder[String].contramap(_.entryName)

  implicit val jWSAlgorithmTypeJsonDecoder: Decoder[JWSAlgorithmType] =
    Decoder[String].emap(JWSAlgorithmType.withNameEither)

}
