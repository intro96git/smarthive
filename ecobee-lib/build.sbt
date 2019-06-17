import Dependencies._

lazy val akkaHttpVersion = "10.1.5"

name := "ecobee-lib"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-stream"          % AkkaVersion,
  AkkaSlf4j,
  
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "com.typesafe" % "config" % "1.3.3",
  
  Monix,
  
  Slf4j,
  
  JodaTime,
  JodaConvert,
  ScalaTime,

  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit"         % AkkaVersion     % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % AkkaVersion     % Test,
  ScalaTest
)
