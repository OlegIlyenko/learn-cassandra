name := "learn-cassandra"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint", "-Xlint:-missing-interpolator")

resolvers ++= Seq(Resolver.bintrayRepo("websudos", "oss-releases"))

val phantomVersion = "1.28.12"
val akkaVersion = "2.4.8"

libraryDependencies ++= Seq(
  "com.websudos" %% "phantom-dsl" % phantomVersion,
  "com.websudos" %% "phantom-reactivestreams" % phantomVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion)
