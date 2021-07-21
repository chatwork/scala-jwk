package com.chatwork.scala.jwk

trait AlgorithmType extends Product with Serializable {
  val entryName: String
  val requirement: Requirement
}

trait AlgorithmTypeFactory[A <: AlgorithmType] {

  case object None extends AlgorithmType {
    override val entryName                = "none"
    override val requirement: Requirement = Requirement.Required
  }

}
