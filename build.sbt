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
    libraryDependencies      += "dev.profunktor"    %% "redis4cats-effects"    % "1.5.2",
    libraryDependencies      += "dev.profunktor"    %% "redis4cats-streams"    % "1.5.2",
    libraryDependencies      += "io.circe"          %% "circe-core"            % "0.14.6",
    libraryDependencies      += "io.circe"          %% "circe-generic"         % "0.14.6",
    libraryDependencies      += "io.circe"          %% "circe-parser"          % "0.14.6"
  )
