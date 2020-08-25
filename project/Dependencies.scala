import sbt._

object Dependencies {

  private val AkkaVersion     = "2.6.8"
  private val AkkaHttpVersion = "10.2.0"
  private val CirceVersion    = "0.12.3"

  private val akkaActor     = "com.typesafe.akka" %% "akka-actor"      % AkkaVersion
  private val akkaStream    = "com.typesafe.akka" %% "akka-stream"     % AkkaVersion
  private val akkaHttpCore  = "com.typesafe.akka" %% "akka-http-core"  % AkkaHttpVersion
  private val circeCore     = "io.circe"          %% "circe-core"      % CirceVersion
  private val circeGeneric  = "io.circe"          %% "circe-generic"   % CirceVersion
  private val circeParser   = "io.circe"          %% "circe-parser"    % CirceVersion
  private val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.34.0" // needs to match with akka-http
  private val freestyleAkka = "io.frees"          %% "frees-akka"      % "0.8.2"
  private val slf4j         = "org.slf4j"          % "slf4j-api"       % "1.7.30"
  private val logback       = "ch.qos.logback"     % "logback-classic" % "1.2.3"

  val Simple = Seq(
    akkaActor,
    akkaStream,
    akkaHttpCore,
    circeCore,
    circeGeneric,
    circeParser,
    akkaHttpCirce,
    freestyleAkka,
    slf4j,
    logback
  )
}
