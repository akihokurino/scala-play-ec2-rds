name := """boysbaito_admin_api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.+",
  "org.scalikejdbc" %% "scalikejdbc" % "2.4.+",
  "io.really" %% "jwt-scala" % "1.2.2",
  "io.spray" %% "spray-json" % "1.3.3",
  "com.amazonaws" % "aws-java-sdk" % "1.11.46",
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",
  "net.debasishg" %% "redisclient" % "3.4",
  "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.+"
)

