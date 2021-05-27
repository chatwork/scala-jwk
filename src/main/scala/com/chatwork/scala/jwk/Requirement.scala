package com.chatwork.scala.jwk

import scala.collection.immutable

sealed abstract class Requirement(val entryName: String) extends Product with Serializable

object Requirement {
  def values: immutable.IndexedSeq[Requirement] =
    immutable.IndexedSeq(Required, Recommended, Optional)

  case object Required    extends Requirement("Required")
  case object Recommended extends Requirement("Recommended")
  case object Optional    extends Requirement("Optional")

}
