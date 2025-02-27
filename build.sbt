name := "resy-booking-bot"

scalaVersion := "2.13.8"

ThisBuild / scalafixDependencies ++= Seq(
  "com.github.vovapolu" %% "scaluzzi"         % "0.1.23",
  "org.scalatest"       %% "autofix"          % "3.1.0.1",
  "com.eed3si9n.fix"    %% "scalafix-noinfer" % "0.1.0-M1"
)

val root = Project("resy-booking-bot", file("."))
  .settings(
    semanticdbEnabled := true,
    scalacOptions += "-Ywarn-unused",
    libraryDependencies ++= Seq(
      "com.typesafe.play"        %% "play-ahc-ws"     % "2.8.16",
      "com.github.pureconfig"    %% "pureconfig"      % "0.17.1",
      "org.apache.logging.log4j" %% "log4j-api-scala" % "12.0",
      "org.apache.logging.log4j"  % "log4j-core"      % "2.13.0" % Runtime,
      "org.scalatest"            %% "scalatest"       % "3.2.12" % Test,
      "org.mockito"               % "mockito-core"    % "4.6.1"  % Test,
      "org.slf4j"                 % "slf4j-nop"       % "1.7.36"
      // The above removes failed to load class warning
    ),
    publish := {},
    publishLocal := {}
  )

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case PathList("reference.conf") => MergeStrategy.concat
 case _                        => MergeStrategy.first
}
