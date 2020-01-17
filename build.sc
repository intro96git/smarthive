import mill._, scalalib._
import $file.util.{build => u}
import $file.util.{dependencies => d}
import d.Dependencies._
import CommonDeps._

// ####################################################################################################

object CommonDeps {  
  val TypesafeConfig = ivy"com.typesafe:config:1.4.0"
  val H2 = ivy"com.h2database:h2:1.4.200"
  val BetterFiles = ivy"com.github.pathikrit::better-files:3.8.0"

  val DoobieVersion = "0.8.8"
  val DoobieCore = ivy"org.tpolecat::doobie-core:$DoobieVersion"
  val DoobieH2 = ivy"org.tpolecat::doobie-h2:$DoobieVersion"
  val DoobieTest = ivy"org.tpolecat::doobie-scalatest:$DoobieVersion"
  
  val ZIO = ivy"dev.zio::zio:1.0.0-RC17"
  val ZIOCats = ivy"dev.zio::zio-interop-cats:2.0.0.0-RC10"
  val ZIOTwitter = ivy"dev.zio::zio-interop-twitter:19.12.0.0-RC1"
  
  val ScalaMock = ivy"org.scalamock::scalamock:4.4.0"
  
  val AkkaHttpVersion = "10.1.11"
  
  val FinchVersion = "0.31.0"
  val Finagle = ivy"com.twitter::finagle-http:19.12.0"
  val Finch = ivy"com.github.finagle::finchx-core:$FinchVersion"
  val FinchCirce = ivy"com.github.finagle::finchx-circe:$FinchVersion"
  val TwitterServer = ivy"com.twitter::twitter-server:19.12.0"
}

// ####################################################################################################


// uncomment when you need to rebuild Bloop config files for standalone Bloop server
// Then run "mill mill.contrib.Bloop/install"
// Note: this will overwrite any files arleady in the .bloop directory!
// import $ivy.`com.lihaoyi::mill-contrib-bloop:0.5.2` 

object util extends Module {
  object util extends u.util
}

object `ecobee-client` extends u.CommonSbtModule {
  object test extends Tests {
    def ivyDeps = Agg(
      ScalaTest,
      Logback,
      ivy"com.typesafe.akka::akka-http-spray-json:$AkkaHttpVersion",
      ivy"com.typesafe.akka::akka-http:$AkkaHttpVersion",
      ivy"com.typesafe.akka::akka-http-xml:$AkkaHttpVersion",
      ivy"com.typesafe.akka::akka-stream:$AkkaVersion"
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")
    
    // from https://github.com/lihaoyi/mill/issues/344#issuecomment-445741323
    def testOne(args: String*) = T.command {
      super.runMain("org.scalatest.run", args: _*)
    }
  }
  
  def moduleDeps = Seq(util.util)

  def ivyDeps = Agg(
    Finagle, 

    Slf4j, ScalaLogging,

    TypesafeConfig,

    BetterFiles,

    H2, DoobieCore, DoobieH2,

    ZIO, ZIOCats, ZIOTwitter,

    JodaTime, JodaConvert, ScalaTime
  )
}

object `ecobee-ext` extends u.CommonModule {

  

  object test extends Tests {
    def ivyDeps = Agg(ScalaTest, ScalaMock)
    def testFrameworks = Seq("org.scalatest.tools.Framework")
    def moduleDeps = Seq(`ecobee-ext`,`ecobee-client`.test)

    
    // from https://github.com/lihaoyi/mill/issues/344#issuecomment-445741323
    def testOne(args: String*) = T.command {
      super.runMain("org.scalatest.run", args: _*)
    }
  }
  
  def moduleDeps = Seq(`ecobee-client`, util.util)

  def ivyDeps = Agg(
    Finch,
    FinchCirce,
    TwitterServer,
    TypesafeConfig,
    H2, DoobieCore, DoobieH2,
    ZIO, ZIOCats,
    JodaTime, JodaConvert, ScalaTime
  )
}

