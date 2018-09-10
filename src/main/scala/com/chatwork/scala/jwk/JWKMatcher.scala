package com.chatwork.scala.jwk

import com.chatwork.scala.jwk.JWKError.JOSEError
import com.github.j5ik2o.base64scala.Base64String

case class JWKMatcher(keyTypes: Set[KeyType],
                      uses: Set[PublicKeyUseType],
                      operation: Set[KeyOperationType],
                      algs: Set[AlgorithmType],
                      ids: Set[String],
                      hasUse: Boolean,
                      hasId: Boolean,
                      privateOnly: Boolean,
                      publicOnley: Boolean,
                      minSizeBits: Int,
                      maxSizeBits: Int,
                      sizesBits: Set[Int],
                      curves: Set[Curve],
                      x5tS256s: Set[Base64String]) {
  def matches(key: JWK): Either[JOSEError, Boolean] = {
    if (hasUse && key.publicKeyUseType.isEmpty)
      Right(false)
    else if (hasId && (key.keyId.isEmpty || key.keyId.exists(_.value.trim.isEmpty)))
      Right(false)
    else if (privateOnly && !key.isPrivate)
      Right(false)
    else if (publicOnley && key.isPrivate)
      Right(false)
    else if (keyTypes.nonEmpty && !keyTypes.contains(key.keyType))
      Right(false)
    else if (uses.nonEmpty && !uses.exists(v => key.publicKeyUseType.contains(v)))
      Right(false)
    else if (operation.nonEmpty) {
      if (operation.isEmpty && key.keyOperations.isEmpty)
        Right(true)
      else if (key.keyOperations.nonEmpty && operation.forall(v => key.keyOperations.contains(v)))
        Right(true)
      else Right(false)
    } else if (algs.nonEmpty && algs.exists(v => key.algorithmType.contains(v)))
      Right(false)
    else if (ids.nonEmpty && ids.exists(v => key.keyId.contains(v)))
      Right(false)
    else if (minSizeBits > 0)
      key.size.map(_ < minSizeBits)
    else if (maxSizeBits > 0)
      key.size.map(_ > maxSizeBits)
    else if (sizesBits.nonEmpty)
      key.size.map(v => !sizesBits.contains(v))
    else if (curves.nonEmpty)
      if (!key.isInstanceOf[CurveBasedJWK])
        Right(false)
      else if (!curves.contains(key.asInstanceOf[CurveBasedJWK].curve))
        Right(false)
      else Right(true)
    else if (x5tS256s.nonEmpty)
      if (!x5tS256s.exists(v => key.x509CertificateSHA256Thumbprint.contains(v)))
        Right(false)
      else Right(true)
    else
      Right(true)
  }
}
