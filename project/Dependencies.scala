import sbt._
import sbt.ModuleID

object Versions {
  val scala212Version = "2.12.13"
  val scala213Version = "2.13.6"
  val scala3Version   = "3.0.1"
}

object scalaLang {
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.0"
}

object circe {
  val version           = "0.14.1"
  val core: ModuleID    = "io.circe" %% "circe-core"    % version
  val parser: ModuleID  = "io.circe" %% "circe-parser"  % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
}

object scalatest {
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.9"
}

object scalacheck {
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.15.4"
}

object j5ik2o {
  val base64scala = "com.github.j5ik2o" %% "base64scala" % "1.0.25"
}
