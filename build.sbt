name := "MakeModelVersion"

version := "1.0"

scalaVersion := "2.11.7"

mainClass := Some("Main")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % "2.4.0",
  "com.typesafe.play" %% "play-json" % "2.4.0"
)
    