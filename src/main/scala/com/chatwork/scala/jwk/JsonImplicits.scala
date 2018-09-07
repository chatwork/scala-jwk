package com.chatwork.scala.jwk

trait JsonImplicits
    extends JWSAlgorithmTypeJsonImplicits
    with KeyIdJsonImplicits
    with KeyTypeJsonImplicits
    with PublicKeyUseJsonImplicits
    with KeyOperationsJsonImplicits
    with Base64StringJsonImplicits
