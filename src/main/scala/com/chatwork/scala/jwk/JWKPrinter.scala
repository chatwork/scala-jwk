package com.chatwork.scala.jwk

import io.circe.Printer

object JWKPrinter {

  val noSpaces: Printer = Printer(
    preserveOrder = true,
    dropNullValues = true,
    indent = ""
  )

  val space2 = Printer.spaces2.copy(dropNullValues = true)
}
