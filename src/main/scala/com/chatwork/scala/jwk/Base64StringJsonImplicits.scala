package com.chatwork.scala.jwk

import com.github.j5ik2o.base64scala.Base64String
import io.circe.{Decoder, Encoder}

trait Base64StringJsonImplicits {

  implicit val Base64StringJsonEncoder: Encoder[Base64String] = Encoder[String].contramap(_.asString)

  implicit val Base64StringJsonDecoder: Decoder[Base64String] = Decoder[String].map(Base64String(_, urlSafe = true))

}
