package com.chatwork.scala.jwk

import io.circe.syntax._
import io.circe.{parser, Decoder, Encoder, Json}
import cats.syntax.either._
import com.chatwork.scala.jwk.JWKError.{JOSEError, JWKSetCreationError}

import scala.collection.immutable.SortedSet

case class JWKSet(breachEncapsulationOfValues: SortedSet[JWK]) {

  def keyByKeyId(keyId: String): Option[JWK] = {
    breachEncapsulationOfValues.find(_.keyId.contains(keyId))
  }

  def select(matcher: JWKMatcher): Either[JOSEError, Seq[JWK]] = {
    if (breachEncapsulationOfValues.isEmpty) {
      Right(Seq.empty)
    } else {
      breachEncapsulationOfValues.foldLeft[Either[JOSEError, Seq[JWK]]](Right(Seq.empty[JWK])) { (result, element) =>
        for {
          r <- result
          e <- matcher.matches(element)
        } yield if (e) r :+ element else r
      }
    }
  }

  def size: Int = breachEncapsulationOfValues.size

  lazy val toPublicJWKSet: JWKSet = JWKSet(breachEncapsulationOfValues.map(_.toPublicJWK))

  def toJsonString(implicit encoder: Encoder[JWKSet]): String = {
    JWKPrinter.noSpaces.pretty(this.asJson)
  }

  def toJsonStringWithSpace(implicit encoder: Encoder[JWKSet]): String = {
    JWKPrinter.space2.pretty(this.asJson)
  }

}

object JWKSet extends JWKSetJsonImplicits {

  def apply(jwk: JWK): JWKSet = new JWKSet(SortedSet(jwk))

  def apply(jwks: JWK*): JWKSet = new JWKSet(SortedSet(jwks: _*))

  def fromSeq(jwks: Seq[JWK]): JWKSet = new JWKSet(SortedSet(jwks: _*))

  def fromSet(jwks: Set[JWK]): JWKSet = fromSeq(jwks.toSeq)

  def parseFromString(text: String): Either[JWKSetCreationError, JWKSet] = {
    parser.parse(text) match {
      case Left(error) =>
        Left(JWKSetCreationError(error.getMessage(), None))
      case Right(json) =>
        parseFromJson(json)
    }
  }

  def parseFromJson(json: Json): Either[JWKSetCreationError, JWKSet] = {
    json.as[JWKSet].leftMap(error => JWKSetCreationError(error.getMessage(), None))
  }

}

trait JWKSetJsonImplicits extends JWKJsonImplicits {
  import io.circe.syntax._
  import cats.syntax.either._

  implicit val JWKSetJsonEncoder: Encoder[JWKSet] = Encoder.instance { v =>
    Json.obj(
      "keys" -> v.breachEncapsulationOfValues.asJson
    )
  }

  implicit val JWKSetJsonDecoder: Decoder[JWKSet] = Decoder.instance(_.get[Seq[JWK]]("keys").map { v =>
    JWKSet.fromSeq(v)
  })

}
