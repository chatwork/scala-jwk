package com.chatwork.scala.jwk

object KeyUseAndOpsConsistency {

  val rules: Map[PublicKeyUseType, Set[KeyOperationType]] = Map(
    PublicKeyUseType.Signature -> Set(
      KeyOperationType.Sign,
      KeyOperationType.Verify
    ),
    PublicKeyUseType.Encryption -> Set(
      KeyOperationType.Encrypt,
      KeyOperationType.Decrypt,
      KeyOperationType.WrapKey,
      KeyOperationType.UnwrapKey
    )
  )

  def areConsistent(use: PublicKeyUseType, ops: KeyOperations): Boolean = {
    ops.breachEncapsulationOfValues.forall { v =>
      rules(use).contains(v)
    }
  }

}
