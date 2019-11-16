import Dependencies._

lazy val akkaHttpVersion = "10.1.10"

name := "ecobee-lib"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-stream"          % AkkaVersion,
  AkkaSlf4j,

  "com.typesafe" % "config" % "1.3.3",

  "com.github.pathikrit" %% "better-files" % "3.8.0",

  "com.h2database" % "h2" % "1.4.199",

  ZIO,

  "org.tpolecat" %% "doobie-core"      % DoobieVersion,
  "org.tpolecat" %% "doobie-h2"        % DoobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % DoobieVersion,

  Slf4j,

  JodaTime,
  JodaConvert,
  ScalaTime,

  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit"         % AkkaVersion     % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % AkkaVersion     % Test,
  ScalaTest,

  ammonite(scalaBinaryVersion.value)
)

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue
