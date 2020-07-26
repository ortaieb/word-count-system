name := "word_count_system"

organization := "com.ortaieb"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.1"

val fs2Version        = "2.4.0"
val circeVersion      = "0.13.0"
val http4sVersion     = "0.21.6"
val pureConfigVersion = "0.12.0"
val scalatestVersion  = "3.1.0"

libraryDependencies ++= Seq(
  "co.fs2"                %% "fs2-core"             % fs2Version,
  "co.fs2"                %% "fs2-io"               % fs2Version,
  "io.circe"              %% "circe-core"           % circeVersion,
  "io.circe"              %% "circe-generic"        % circeVersion,
  "io.circe"              %% "circe-generic-extras" % circeVersion,
  "io.circe"              %% "circe-parser"         % circeVersion,
  "org.http4s"            %% "http4s-blaze-server"  % http4sVersion,
  "org.http4s"            %% "http4s-dsl"           % http4sVersion,
  "org.http4s"            %% "http4s-circe"         % http4sVersion,
  "com.github.pureconfig" %% "pureconfig"           % pureConfigVersion,
  "org.scalatest"         %% "scalatest"            % scalatestVersion  % "test"
)

scalafmtOnCompile := true

initialCommands := "import com.ortaieb.wordcountsystem._"

mainClass in assembly := Some("com.ortaieb.wordcountsystem.Main")
test in assembly := {}
assemblyJarName in assembly := "word-count.jar"