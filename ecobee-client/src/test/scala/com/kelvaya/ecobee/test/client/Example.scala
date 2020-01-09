package com.kelvaya.ecobee.test.client

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.tokens.Tokens

import com.kelvaya.ecobee.client.service.Select
import com.kelvaya.ecobee.client.service.SelectType

/** Expects one argument with the path to the token storage file 
  * 
  * @note If executing through mill, you will need to create an `application.conf` file that exists on the classpath
  * (such as adding it to the `test/resources` directory).
  */
object Example extends App {

  if (this.args.length != 2) doMsg
  else go(args)
  
  private lazy val Account = new AccountID("example") 
  private lazy val createUser = zio.ZIO.accessM[ClientEnv](_.tokenStorage.storeTokens(Account, Tokens(None, None, None)))


  private def go(args : Array[String]) = {

    val runtime = new zio.DefaultRuntime { }

    runtime.unsafeRun {

      val file = better.files.File(this.args(1))
      val newFile = 
        if (file.exists) false 
        else {
          val _ = file.createFile()
          true
        }

      createEnvUsingFileStore(file).flatMap { env =>

        implicit val s = env.settings

        val main = args(0) match {
          case "pin"     => Right(doPin _)
          case "init"    => Right(doInit _)
          case "refresh" => Right(doRefresh _)
          case "temp"    => Right(doTemp _)
          case _         => Left(doMsg)
        }

        main.fold(
          msg => msg,
          fn => {        
            val req = for {
              _   <- if (newFile) createUser else zio.UIO.unit
              _   <- fn()
            } yield 0

            req.provide(env)
          }
        )
      }
      .catchAll[Any,Nothing,Int] {
        case t : Throwable => 
          Console.err.println(s"Unexpected error returned : $t")
          zio.UIO(2)
      }
    }
  }



  def doPin()(implicit s : ClientSettings.Service[Any]) = {        
    for {
      pin <- service.PinService.execute
      _   <- zio.UIO(println(s"Pin Response: $pin"))
      _   <- zio.ZIO.accessM[ClientEnv](_.tokenStorage.storeTokens(Account, Tokens(Some(pin.code),None,None)))
    } yield 0
  }

  def doInit()(implicit s : ClientSettings.Service[Any]) = {
    for {
      tok <- service.InitialTokensService.execute(Account)
      _   <- zio.UIO(println(s"Token Response: $tok"))
      _   <- zio.ZIO.accessM[ClientEnv](_.tokenStorage.storeTokens(Account, Tokens(None,Some(tok.access_token),Some(tok.refresh_token))))
    } yield 0
  }
  
  
  def doRefresh()(implicit s : ClientSettings.Service[Any]) = {
    for {
      tok <- service.RefreshTokensService.execute(Account)
      _   <- zio.UIO(println(s"Refresh Token Response: $tok"))
      _   <- zio.ZIO.accessM[ClientEnv](_.tokenStorage.storeTokens(Account, Tokens(None,Some(tok.access_token),Some(tok.refresh_token))))
    } yield 0
  }


  def doTemp()(implicit s : ClientSettings.Service[Any]) = {
    for {
      res <- service.ThermostatService.execute(Account, Select(SelectType.Registered, includeRuntime=true))
      msg =  res.thermostatList.map(t => s"${t.name} @ ${Temperature(t.runtime.get.rawTemperature).C}℃ ").mkString(" :: ")
      _   <- zio.UIO(println(s"Therm Response: $msg"))
    } yield 0
  }



  private def doMsg = {
    val msg = """ 
    | scala com.kelvaya.ecobee.test.client.Example <cmd> <path-to-token-store-file>
    |
    | where:
    |   <cmd> is one of [pin|init]
    """.stripMargin

    println(msg)
    zio.UIO(1)
  }
}