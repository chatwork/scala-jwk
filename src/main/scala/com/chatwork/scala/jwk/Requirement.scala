package com.chatwork.scala.jwk

import enumeratum._

import scala.collection.immutable

sealed trait Requirement extends EnumEntry

object Requirement extends Enum[Requirement] {
  override def values: immutable.IndexedSeq[Requirement] = findValues

  case object Required    extends Requirement
  case object Recommended extends Requirement
  case object Optional    extends Requirement

}
