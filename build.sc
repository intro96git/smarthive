import mill._, scalalib._
import $file.util.{build => u}
import $file.util.{dependencies => d}
import d.Dependencies._

// uncomment when you need to rebuild Bloop config files for standalone Bloop server
// Then run "mill mill.contrib.Bloop/install"
// Note: this will overwrite any files arleady in the .bloop directory!
// import $ivy.`com.lihaoyi::mill-contrib-bloop:0.5.2` 

object util extends Module {
  object util extends u.util
}

object `ecobee-lib` extends u.CommonModule {
  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.typesafe.akka::akka-http-testkit:$AkkaHttpVersion;classifier=Test",
      ivy"com.typesafe.akka::akka-testkit:$AkkaVersion;classifier=Test",
      ivy"com.typesafe.akka::akka-stream-testkit:$AkkaVersion;classifier=Test",
      ScalaTest
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
  
  def moduleDeps = Seq(util.util)

  lazy val AkkaHttpVersion = "10.1.10"

  def ivyDeps = Agg(
    ivy"com.typesafe.akka::akka-http-spray-json:$AkkaHttpVersion",
    ivy"com.typesafe.akka::akka-http:$AkkaHttpVersion",
    ivy"com.typesafe.akka::akka-http-xml:$AkkaHttpVersion",

    ivy"com.typesafe.akka::akka-stream:$AkkaVersion",
    AkkaSlf4j,

    ivy"com.typesafe:config:1.3.3",

    ivy"com.github.pathikrit::better-files:3.8.0",

    ivy"com.h2database:h2:1.4.199",

    ZIO,
    ZIOCats,

    ivy"org.tpolecat::doobie-core:$DoobieVersion",
    ivy"org.tpolecat::doobie-h2:$DoobieVersion",
    ivy"org.tpolecat::doobie-scalatest:$DoobieVersion",

    Slf4j,

    JodaTime,
    JodaConvert,
    ScalaTime,
  )
}
