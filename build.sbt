sonatypeProfileName := "com.chatwork"

organization := "com.chatwork"

name := "scala-jwk"

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.11", "2.12.8")

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-language:_",
) ++ {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2L, scalaMajor)) if scalaMajor == 12 =>
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
  ScalaTest.v3_0_1      % Test,
  ScalaCheck.scalaCheck % Test,
  Cats.v1_6_1,
  Enumeratum.latest,
  Scala.java8Compat,
  J5ik2o.base64scala,
  Circe.core,
  Circe.generic,
  Circe.parser
)

updateOptions := updateOptions.value.withCachedResolution(true)

parallelExecution in Test := false

javaOptions in (Test, run) ++= Seq("-Xms4g", "-Xmx4g", "-Xss10M", "-XX:+CMSClassUnloadingEnabled")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

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

publishTo in ThisBuild := sonatypePublishTo.value

credentials := {
  val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
  Credentials(ivyCredentials) :: Nil
}
