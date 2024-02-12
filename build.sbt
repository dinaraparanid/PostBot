ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "TgPostBot",
    idePackagePrefix := Some("com.paranid5.tgpostbot"),
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
    libraryDependencies += "com.github.pengrad" % "java-telegram-bot-api" % "7.0.1",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3"
  )
