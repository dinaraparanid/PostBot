ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "TgPostBot",
    idePackagePrefix := Some("com.paranid5.tgpostbot"),
    libraryDependencySchemes += "org.typelevel"     %% "cats-effect"           % VersionScheme.Always,
    libraryDependencySchemes += "io.monix"          %% "monix"                 % VersionScheme.Always,
    libraryDependencySchemes += "org.tpolecat"      %% "doobie-core"           % VersionScheme.Always,
    libraryDependencySchemes += "org.tpolecat"      %% "doobie-postgres"       % VersionScheme.Always,
    libraryDependencies      += "org.typelevel"     %% "cats-core"             % "2.10.0",
    libraryDependencies      += "com.github.pengrad" % "java-telegram-bot-api" % "7.0.1",
    libraryDependencies      += "org.typelevel"     %% "cats-effect"           % "3.5.3",
    libraryDependencies      += "io.monix"          %% "monix"                 % "3.4.1",
  )
  .aggregate(
    core,
    data
  )

lazy val core = project
  .settings(
    name := "core",
    idePackagePrefix := Some("com.paranid5.tgpostbot.core"),
  )

lazy val data = project
  .settings(
    name := "data",
    idePackagePrefix := Some("com.paranid5.tgpostbot.data"),
    libraryDependencySchemes += "org.typelevel" %% "cats-effect"     % VersionScheme.Always,
    libraryDependencySchemes += "io.monix"      %% "monix"           % VersionScheme.Always,
    libraryDependencySchemes += "org.tpolecat"  %% "doobie-core"     % VersionScheme.Always,
    libraryDependencySchemes += "org.tpolecat"  %% "doobie-postgres" % VersionScheme.Always,
    libraryDependencies      += "io.monix"      %% "monix"           % "3.4.1",
    libraryDependencies      += "org.tpolecat"  %% "doobie-core"     % "1.0.0-RC4",
    libraryDependencies      += "org.tpolecat"  %% "doobie-postgres" % "1.0.0-RC4",
  )
  .dependsOn(core)