package com.chatwork.scala.jwk

import io.circe.{Decoder, Encoder}

case class KeyOperations(breachEncapsulationOfValues: Seq[KeyOperationType]) {
  def isEmpty: Boolean                           = breachEncapsulationOfValues.isEmpty
  def nonEmpty: Boolean                          = !isEmpty
  def contains(value: KeyOperationType): Boolean = breachEncapsulationOfValues.contains(value)
}

object KeyOperations {

  val empty = KeyOperations(Seq.empty)

  def fromSingle(value: KeyOperationType): KeyOperations = new KeyOperations(Seq(value))

  def fromParams(values: KeyOperationType*): KeyOperations = new KeyOperations(values.toSeq)

}

trait KeyOperationsJsonImplicits extends KeyOperationTypeJsonImplicits {

  implicit val KeyOperationsJsonEncoder: Encoder[KeyOperations] =
    Encoder[Seq[KeyOperationType]].contramap(_.breachEncapsulationOfValues)

  implicit val KeyOperationsJsonDecoder: Decoder[KeyOperations] = Decoder[Seq[KeyOperationType]].map(KeyOperations(_))

}
