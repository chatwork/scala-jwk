package com.chatwork.scala.jwk

trait JsType[+A] {

  val value: A

}

object JsType {

  case object JsNull extends JsType[Null] {
    override val value: Null = null
  }

  case class JsBoolean(value: Boolean) extends JsType[Boolean]

  case class JsString(value: String) extends JsType[String]

  case class JsNumber(value: Long) extends JsType[Long]

  case class JsArray[A <: JsType[_]](value: Seq[A]) extends JsType[Seq[A]]

  case class JsMap[A <: JsType[_]](value: Map[String, A]) extends JsType[Map[String, A]]

}
