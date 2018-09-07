package com.chatwork.scala.jwk

import enumeratum._

trait AlgorithmType extends EnumEntry {
  val requirement: Requirement
}

trait AlgorithmTypeFactory[A <: AlgorithmType] extends Enum[A] {

  case object None extends AlgorithmType {
    override val entryName                = "none"
    override val requirement: Requirement = Requirement.Required
  }

}
