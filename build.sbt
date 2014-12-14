name := """Shining LINE"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.7" % "test",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.2",
  "org.julienrf" %% "play-jsonp-filter" % "1.2",
  "org.xerial" % "sqlite-jdbc" % "3.8.7"
)

