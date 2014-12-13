name := """Shining LINE"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.2"
)

