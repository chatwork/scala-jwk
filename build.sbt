
organization := "com.chatwork"

name := "scala-jwk"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-Xfatal-warnings",
  "-language:_",
  // Warn if an argument list is modified to match the receiver
  "-Ywarn-adapted-args",
  // Warn when dead code is identified.
  "-Ywarn-dead-code",
  // Warn about inaccessible types in method signatures.
  "-Ywarn-inaccessible",
  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-infer-any",
  // Warn when non-nullary `def f()' overrides nullary `def f'
  "-Ywarn-nullary-override",
  // Warn when nullary methods return Unit.
  "-Ywarn-nullary-unit",
  // Warn when numerics are widened.
  "-Ywarn-numeric-widen",
  // Warn when imports are unused.
  "-Ywarn-unused-import"
)

resolvers ++= Seq(
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
)
val circeVersion    = "0.10.0-M1"



libraryDependencies ++= Seq(
  ScalaTest.v3_0_1 % Test,
  ScalaCheck.scalaCheck % Test,
  Cats.v1_1_0,
  Enumeratum.latest,
  Scala.java8Compat,
  "com.github.j5ik2o" %% "base64scala" % "1.0.0"
) ++ Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

updateOptions := updateOptions.value.withCachedResolution(true)

parallelExecution in Test := false

javaOptions in(Test, run) ++= Seq("-Xms4g", "-Xmx4g", "-Xss10M", "-XX:+CMSClassUnloadingEnabled")
