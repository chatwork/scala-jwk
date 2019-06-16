import sbt._
import sbt.ModuleID

object Scala {
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"
}

object Circe {
  val version           = "0.11.1"
  val core: ModuleID    = "io.circe" %% "circe-core" % version
  val parser: ModuleID  = "io.circe" %% "circe-parser" % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
}

object ScalaTest {
  val v3_0_1 = "org.scalatest" %% "scalatest" % "3.0.1"
}

object Cats {
  val v1_6_1 ="org.typelevel"         %% "cats-core"            % "1.6.1"
}

object Enumeratum {
  val latest = "com.beachape" %% "enumeratum" % "1.5.13"
}

object ScalaCheck {
  val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"
}

object J5ik2o {
  val base64scala = "com.github.j5ik2o" %% "base64scala" % "1.0.4" excludeAll ExclusionRule(organization = "io.circe")
}
