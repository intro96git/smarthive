import mill._, scalalib._
import $file.util.{build => u}
import $file.util.{dependencies => d}
import d.Dependencies._
import CommonDeps._

// uncomment when you need to rebuild Bloop config files for standalone Bloop server
// Then run "mill mill.contrib.Bloop/install"
// Note: this will overwrite any files arleady in the .bloop directory!
// import $ivy.`com.lihaoyi::mill-contrib-bloop:0.5.2` 

object util extends Module {
  object util extends u.util
}

object `ecobee-client` extends u.CommonSbtModule {
  object test extends Tests {
    def ivyDeps = Agg(ScalaTest)
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
  
  def moduleDeps = Seq(util.util)

  def ivyDeps = Agg(
    ivy"com.typesafe.akka::akka-http-spray-json:$AkkaHttpVersion",
    ivy"com.typesafe.akka::akka-http:$AkkaHttpVersion",
    ivy"com.typesafe.akka::akka-http-xml:$AkkaHttpVersion",
    ivy"com.typesafe.akka::akka-stream:$AkkaVersion",
    AkkaSlf4j, Slf4j,

    TypesafeConfig,

    ivy"com.github.pathikrit::better-files:3.8.0",

    H2, DoobieCore, DoobieH2,

    ZIO, ZIOCats,

    JodaTime, JodaConvert, ScalaTime
  )
}

object `ecobee-ext-server` extends u.CommonModule {
  object test extends Tests {
    def ivyDeps = Agg(ScalaTest)
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
  
  def moduleDeps = Seq(`ecobee-client`, util.util)

  def ivyDeps = Agg(
    AkkaSlf4j, Slf4j,
    TypesafeConfig,
    H2, DoobieCore, DoobieH2,
    ZIO, ZIOCats,
    JodaTime, JodaConvert, ScalaTime
  )
}


// ####################################################################################################

object CommonDeps {  
  val TypesafeConfig = ivy"com.typesafe:config:1.3.3"
  val H2 = ivy"com.h2database:h2:1.4.199"
  
  val DoobieVersion = "0.8.4"
  val DoobieCore = ivy"org.tpolecat::doobie-core:$DoobieVersion"
  val DoobieH2 = ivy"org.tpolecat::doobie-h2:$DoobieVersion"
  val DoobieTest = ivy"org.tpolecat::doobie-scalatest:$DoobieVersion"
  
  val ZIO = ivy"dev.zio::zio:1.0.0-RC16"
  val ZIOCats = ivy"dev.zio::zio-interop-cats:2.0.0.0-RC7"
  
  val AkkaHttpVersion = "10.1.10"
  val AkkaHttpTest = ivy"com.typesafe.akka::akka-http-testkit:$AkkaHttpVersion"
  val AkkaTestKit = ivy"com.typesafe.akka::akka-testkit:$AkkaVersion"
  val AkkaStreamTest = ivy"com.typesafe.akka::akka-stream-testkit:$AkkaVersion"
}
