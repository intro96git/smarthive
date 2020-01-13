package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.service._
import com.kelvaya.ecobee.client.service.ThermostatService._
import com.kelvaya.ecobee.client.tokens.Tokens

import com.typesafe.scalalogging.Logger

import zio.ZIO
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError

/** Ecobee API client used by the server 
  * 
  * @see [[ApiClientImpl]]
  */
trait ApiClient {

  /** Ecobee API client used by the server */
  val apiClient : ApiClient.Service[Any]
}

/** Module defining API client used by the server */
object ApiClient {

  /** Ecobee API client used by the server 
    * 
    * @tparam R Environment needed to provide access for this client
    * 
    * @see [[ApiClientImpl]]
    */
  trait Service[R] {

    /** Returns an [[AuthStatus]] based on the user registering the app with a PIN. */
    def authorize : ZIO[R,ClientError,AuthStatus]

    /** Return the current statistics for the given thermostat 
      *
      * If the thermostat is not found, the implementation should return [[ClientError.ThermostatNotFound]]
      * 
      * @param id The ID of the thermostat 
      */
    def readThermostat(id : ThermostatID) : ZIO[R,ClientError,ThermostatStats]

    /** Return the current statistics for all registered thermostat */
    def readThermostats : ZIO[R,ClientError,Iterable[ThermostatStats]]

    /** Return registration PIN which must be entered by the user into the app */
    def register : ZIO[R,ClientError,Registration]
  }
}


/** Default implementation of the Ecobee client API, using the Ecobee client library's [[RequestExecutor]] 
  *  to provide access to the thermostat data.
  * 
  * @note One can use [[AplClientImpl$#create]] to instantiate a new instance.
  */ 
trait ApiClientImpl extends ApiClient {
  val account : AccountID
  val env : ClientEnv

  private implicit lazy val _settings = env.settings

  val apiClient = new ApiClient.Service[Any] {

    def authorize: ZIO[Any,ClientError,AuthStatus] = {

      def _authorize = 
        InitialTokensService
          .execute(account)
          .foldM( 
            err => err match {
              case AuthError(AuthError.ErrorCodes.AuthorizationExpired.error, _, _) => zio.IO.succeed(AuthStatus.RegistrationExpired)
              case e => zio.IO.fail(ClientError.ApiServiceError(e))
            },
            _storeTokens
          )
          .provide(env)

      
      def _storeTokens(r : TokensResponse) : zio.IO[ClientError,AuthStatus] =
        env.tokenStorage.storeTokens(account, Tokens(None, Some(r.access_token), Some(r.refresh_token)))
          .map(_ => AuthStatus.Succeeded(r.expires_in))
          .catchAll { err =>
            zio.IO.fail {
              Logger[ApiClientImpl].error(s"Cannot use API client; unexpected token storage failure on save operation: $err") 
              ClientError.ConfigurationError
            } 
          }

      def _tokenTask : zio.IO[ClientError,AuthStatus] =  
        env.tokenStorage.getTokens(account)
          .map { toks => 
            if (toks.accessToken.isDefined && toks.refreshToken.isDefined) AuthStatus.AlreadyAuthorized
            else if (toks.authorizationToken.isDefined) AuthStatus.WaitingForAuthorization
            else AuthStatus.NeedsAuthorization
          }
          .catchAll {
            case TokenStorageError.InvalidAccountError => zio.UIO.succeed(AuthStatus.NeedsAuthorization)
            case err => zio.IO.fail {
              Logger[ApiClientImpl].error(s"Cannot use API client; token storage failure: $err") 
              ClientError.ConfigurationError 
            }
          }


      for {
        stat  <- _tokenTask
        req   <- stat match {
          case AuthStatus.NeedsAuthorization | AuthStatus.WaitingForAuthorization => _authorize
          case _ => zio.IO.succeed(stat)
        }
      } yield req
    }



    def readThermostat(id : ThermostatID) : ZIO[Any,ClientError,ThermostatStats] = {      
      ThermostatService
        .execute(account, Select(SelectType.Thermostats(id.id), includeRuntime=true))
        .mapError(ClientError.ApiServiceError)
        .flatMap { res =>
          zio.IO.fromEither {
            val tempOpt = for {
              t <- res.thermostatList.headOption
              r <- t.runtime
            } yield ThermostatStats(t.name, Temperature(r.rawTemperature))

            tempOpt.map(t => Right(t)).getOrElse(Left(ClientError.ThermostatNotFound))
          }
        }
        .provide(env)
    }



    def readThermostats : ZIO[Any,ClientError,Iterable[ThermostatStats]] = {
      ThermostatService
        .execute(account, Select(SelectType.Registered, includeRuntime=true))
        .mapError(ClientError.ApiServiceError)
        .map(_.thermostatList.flatMap(t => t.runtime.map(r => ThermostatStats(t.name, Temperature(r.rawTemperature))))) 
        .provide(env)
    }



    def register: ZIO[Any,ClientError,Registration] = { 
      val svcTask = 
        PinService
          .execute
          .mapError(ClientError.ApiServiceError)
      
      val go = 
        for {
          t <- svcTask
          _ <- env.tokenStorage
                .storeTokens(account, Tokens(Some(t.code), None, None))
                .catchAll { case tse =>
                  Logger[ApiClientImpl].error(s"Cannot use API client; token storage failure: $tse")
                  zio.IO.fail(ClientError.ConfigurationError)
                }
        } yield Registration(t.ecobeePin, t.expires_in, t.interval)

      go.provide(env)
    }
  }
}


/** Factory for [[ApiClientImpl]]
  * 
  * Use [[#create]] to instantiate a new instance
  */
object ApiClientImpl {

  /** Returns a new [[ApiClientImpl]]
    * which requires a [[ClientEnv]] environment to use.
    * 
    * @param accountId The Account with which the API will connect to the Ecobee servers
    * @param lb (implicit) Akka logging
    */
  def create(accountId : AccountID) : zio.URIO[ClientEnv,ApiClientImpl] = {
    for {
      cenv   <- zio.ZIO.environment[ClientEnv]
      client <- zio.UIO {
        new ApiClientImpl {
          val account = accountId
          val env = cenv
        }
      }
    } yield client
  }
}
