package com.chatwork.scala.jwk

import java.net.URI

import io.circe.{Decoder, Encoder}

trait URIJsonImplicits {
  implicit val UriJsonEncoder: Encoder[URI] = Encoder[String].contramap(_.toString)

  implicit val UriJsonDecoder: Decoder[URI] = Decoder[String].map(URI.create)
}
