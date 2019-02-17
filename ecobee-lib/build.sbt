import Dependencies._

lazy val akkaHttpVersion = "10.1.5"
lazy val akkaVersion    = "2.5.18"

name := "ecobee-lib"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
  
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "com.typesafe" % "config" % "1.3.3",
  
  "org.slf4j" % "slf4j-api" % "1.7.25",
  
  ScalaTime,

  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
  ScalaTest
)
