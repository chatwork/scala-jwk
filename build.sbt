import sbt._

def crossScalacOptions(scalaVersion: String): Seq[String] =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((3L, _)) =>
      Seq(
        "-source:3.0-migration",
        "-Xignore-scala2-macros"
      )
    case Some((2L, scalaMajor)) if scalaMajor >= 12 =>
      Seq(
        "-Ydelambdafy:method",
        "-target:jvm-1.8",
        "-Yrangepos",
        "-Ywarn-unused"
      )
  }

lazy val baseSettings = Seq(
  organization := "com.chatwork",
  homepage     := Some(url("https://github.com/chatwork/scala-jwk")),
  licenses     := List("The MIT License" -> url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      id = "j5ik2o",
      name = "Junichi Kato",
      email = "j5ik2o@gmail.com",
      url = url("https://blog.j5ik2o.me")
    ),
    Developer(
      id = "exoego",
      name = "TATSUNO Yasuhiro",
      email = "ytatsuno.jp@gmail.com",
      url = url("https://www.exoego.net")
    )
  ),
  scalaVersion       := Versions.scala213Version,
  crossScalaVersions := Seq(Versions.scala212Version, Versions.scala213Version, Versions.scala3Version),
  scalacOptions ++= (Seq(
    "-unchecked",
    "-feature",
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-language:_"
  ) ++ crossScalacOptions(scalaVersion.value)),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots")
  ),
  Test / publishArtifact   := false,
  Test / parallelExecution := false,
  Compile / doc / sources := {
    val old = (Compile / doc / sources).value
    if (scalaVersion.value == Versions.scala3Version) {
      Nil
    } else {
      old
    }
  },
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  // Remove me when scalafix is stable and feature-complete on Scala 3
  ThisBuild / scalafixScalaBinaryVersion := (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => CrossVersion.binaryScalaVersion(scalaVersion.value)
    case _            => CrossVersion.binaryScalaVersion(Versions.scala212Version)
  })
)

val library = (project in file("library"))
  .settings(baseSettings)
  .settings(
    name := "scala-jwk",
    libraryDependencies ++= Seq(
      scalatest.scalatest   % Test,
      scalacheck.scalacheck % Test,
      scalaLang.java8Compat,
      j5ik2o.base64scala,
      circe.core,
      circe.generic,
      circe.parser
    )
  )

val root = (project in file("."))
  .settings(baseSettings)
  .settings(
    name           := "scala-jwk-root",
    publish / skip := true
  )
  .aggregate(library)

// --- Custom commands
addCommandAlias("lint", ";scalafmtCheck;test:scalafmtCheck;scalafmtSbtCheck;scalafixAll --check")
addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt;scalafix RemoveUnused")
