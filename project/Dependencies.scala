import sbt._
import sbt.ModuleID

object scalaLang {
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1"
}

object circe {
  val version           = "0.13.0"
  val core: ModuleID    = "io.circe" %% "circe-core"    % version
  val parser: ModuleID  = "io.circe" %% "circe-parser"  % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
}

object scalatest {
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.2"
}

object beachape {
  val enumeratum = "com.beachape" %% "enumeratum" % "1.5.15"
}

object scalacheck {
  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.3"
}

object j5ik2o {
  val base64scala = "com.github.j5ik2o" %% "base64scala" % "1.0.5"
}
