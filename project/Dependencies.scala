import sbt._

object Dependencies {
  lazy val akkaHttpVersion = "10.1.8"
  lazy val akkaVersion    = "2.5.21"

  // Tests
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  lazy val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  lazy val apacheFtpServer = "org.apache.ftpserver" % "ftpserver" % "1.1.1" pomOnly() excludeAll ExclusionRule(organization = "org.slf4j")

  // Prod
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  lazy val alpakkaFile = "com.lightbend.akka" %% "akka-stream-alpakka-file" % "1.0.0"
  lazy val alpakkaFtp = "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % "1.0.0+27-55e8cb9d"

  lazy val shortId = "me.nimavat" % "shortid" % "1.0.1.RC1"
}
