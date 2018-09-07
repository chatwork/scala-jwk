import sbt._
import sbt.ModuleID

object Scala {
  val java8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"
}

object Circe {
  val version           = "0.8.0"
  val core: ModuleID    = "io.circe" %% "circe-core" % version
  val parser: ModuleID  = "io.circe" %% "circe-parser" % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
}

object Logback {
  val version           = "1.2.3"
  val classic: ModuleID = "ch.qos.logback" % "logback-classic" % version
}

object Commons {
  val version = "1.10"
  val codec   = "commons-codec" % "commons-codec" % version
}

object ScalaTest {
  val v3_0_1 = "org.scalatest" %% "scalatest" % "3.0.1"
}

object Cats {
  val v0_9_0 = "org.typelevel" %% "cats" % "0.9.0"
  val v1_1_0="org.typelevel"         %% "cats-core"            % "1.1.0"
}

object Sw4JJ {
  val v1_0_2 = "com.github.j5ik2o" %% "sw4jj" % "1.0.2"
}

object TypesafeConfig {
  val v1_3_1 = "com.typesafe" % "config" % "1.3.1"
}

object Shapeless {
  val v2_3_2 = "com.chuusai" %% "shapeless" % "2.3.2"
}

object SeasarUtil {
  val v0_0_1 = "org.seasar.util" % "s2util" % "0.0.1"
}

object Enumeratum {
  val latest = "com.beachape" %% "enumeratum" % "1.5.12"
}

object Passay {
  val latest = "org.passay" % "passay" % "1.3.0"
}

object AspectJ {
  val v1_8_9 = "org.aspectj" % "aspectjweaver" % "1.8.10"
}

object Kamon {

  val version = "0.6.7"

  val core = "io.kamon" %% "kamon-core" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val autoweave = "io.kamon" %% "kamon-autoweave" % "0.6.5" excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val systemMetrics = "io.kamon" %% "kamon-system-metrics" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val logReporter = "io.kamon" %% "kamon-log-reporter" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val jmx = "io.kamon" %% "kamon-jmx" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val scala = "io.kamon" %% "kamon-scala" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val akka = "io.kamon" %% "kamon-akka-2.5" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val akkaHttp = "io.kamon" %% "kamon-akka-http" % "0.6.8" excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules"),
    ExclusionRule(organization = "com.typesafe.akka")
  )

  val datadog = "io.kamon" %% "kamon-datadog" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val jdbc = "io.kamon" %% "kamon-jdbc" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val all = Seq(core, autoweave, systemMetrics, scala, akka, akkaHttp, datadog, jmx)
}

object GatlingDeps {
  val version                 = "2.2.3"
  val highcharts: ModuleID    = "io.gatling.highcharts" % "gatling-charts-highcharts" % version
  val testFramework: ModuleID = "io.gatling" % "gatling-test-framework" % version
  val app: ModuleID           = "io.gatling" % "gatling-app" % version
}

object AWSSDK {
  val version        = "1.11.169"
  val core: ModuleID = "com.amazonaws" % "aws-java-sdk-core" % version
  val s3: ModuleID   = "com.amazonaws" % "aws-java-sdk-s3" % version
  val sqs: ModuleID  = "com.amazonaws" % "aws-java-sdk-sqs" % version
  val kms: ModuleID  = "com.amazonaws" % "aws-java-sdk-kms" % version
}

object Aws {
  val encryptionSdkJava = "com.amazonaws" % "aws-encryption-sdk-java" % "1.3.1"
}

object SQS {
  val elasticmqRestSqs: ModuleID = "org.elasticmq" %% "elasticmq-rest-sqs" % "0.13.8"
}

object Alpakka {
  val sqs = "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "0.11"
}

object Apache {
  val commonsLang: ModuleID = "org.apache.commons" % "commons-lang3" % "3.1"
}

object Everpeace {
  val healthCheck = "com.github.everpeace" %% "healthchecks-k8s-probes" % "0.3.0"
}

object PureConfig {
  val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.9.1"
}

object T3hnar {
  val bCrypt = "com.github.t3hnar" %% "scala-bcrypt" % "3.1"
}

object ScalaCheck {
  val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"
}
