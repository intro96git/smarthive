lazy val akkaHttpVersion = "10.1.5"
lazy val akkaVersion    = "2.5.18"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.kelvaya",
      scalaVersion    := "2.12.7"
    )),
    name := "ecobee-lib",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      
      "net.codingwell" %% "scala-guice" % "4.2.1",
      "com.typesafe" % "config" % "1.3.3",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    )
  )
