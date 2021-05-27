val scala212Version = "2.12.13"
val scala213Version = "2.13.6"
val scala3Version = "3.0.0"

sonatypeProfileName := "com.chatwork"

organization := "com.chatwork"

name := "scala-jwk"

scalaVersion := scala213Version

crossScalaVersions := Seq(scala212Version, scala213Version, scala3Version)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-language:_"
)

scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3L, _)) =>
      Seq.empty
    case Some((2L, scalaMajor)) if scalaMajor >= 12 =>
      Seq.empty
    case Some((2L, scalaMajor)) if scalaMajor <= 11 =>
      Seq(
        "-Yinline-warnings"
      )
  }
}

resolvers ++= Seq(
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  scalatest.scalatest   % Test,
  scalacheck.scalacheck % Test,
  scalaLang.java8Compat,
  j5ik2o.base64scala,
  circe.core,
  circe.generic,
  circe.parser
)

updateOptions := updateOptions.value.withCachedResolution(true)

Test / parallelExecution := false

Test / run / javaOptions ++= Seq("-Xms4g", "-Xmx4g", "-Xss10M", "-XX:+CMSClassUnloadingEnabled")

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

pomExtra := {
  <url>https://github.com/chatwork/scala-jwk</url>
    <licenses>
      <license>
        <name>The MIT License</name>
        <url>http://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:chatwork/scala-jwk.git</url>
      <connection>scm:git:github.com/chatwork/scala-jwk</connection>
      <developerConnection>scm:git:git@github.com:chatwork/scala-jwk.git</developerConnection>
    </scm>
    <developers>
      <developer>
        <id>j5ik2o</id>
        <name>Junichi Kato</name>
      </developer>
    </developers>
}

publishTo := sonatypePublishToBundle.value

credentials := {
  val ivyCredentials = (LocalRootProject / baseDirectory).value / ".credentials"
  val gpgCredentials = (LocalRootProject / baseDirectory).value / ".gpgCredentials"
  Credentials(ivyCredentials) :: Credentials(gpgCredentials) :: Nil
}
