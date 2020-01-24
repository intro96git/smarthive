package com.kelvaya.ecobee.server

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.service._
import com.kelvaya.ecobee.client.service.ThermostatService._
import com.kelvaya.ecobee.client.tokens.Tokens
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageError

import com.typesafe.scalalogging.Logger

import zio.ZIO

/** Ecobee API client used by the server 
  * 
  * The default service implementation can be found at [[ApiClient$.Live]]
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
    * @see [[ApiClient]]
    */
  trait Service[-R] {

    /** Returns an [[AuthStatus]] based on the user registering the app with a PIN. 
      * 
      * @param account The Extensions account
      */
    def authorize(account : AccountID) : ZIO[R,ClientError,AuthStatus]

    /** Return the current statistics for the given thermostat 
      *
      * If the thermostat is not found, the implementation should return [[ClientError.ThermostatNotFound]]
      * 
      * @param account The Extensions account
      * @param id The ID of the thermostat 
      */
    def readThermostat(account : AccountID, id : ThermostatID) : ZIO[R,ClientError,ThermostatStats]

    /** Return the current statistics for all registered thermostat
      * 
      * @param account The Extensions account
      */
    def readThermostats(account : AccountID) : ZIO[R,ClientError,Iterable[ThermostatStats]]

    /** Return registration PIN which must be entered by the user into the app 
      * 
      * @param account The Extensions account
      */
    def register(account : AccountID) : ZIO[R,ClientError,Registration]
  }


  /** Default implementation of the Ecobee client API, using the Ecobee client library's [[com.kelvaya.ecobee.client.RequestExecutor RequestExecutor]] 
    *  to provide access to the thermostat data.
    * 
    * @note This can be used directly, but requires a [[ClientEnv]] to produce results.  The [[Default]] implementation
    * should be used to integrate that environment.
    */ 
  object Live extends ApiClient.Service[ClientEnv] {
  
    def authorize(account : AccountID): ZIO[ClientEnv,ClientError,AuthStatus] = {

      val _settings = ZIO.access[ClientSettings](_.settings)
      val _storage = ZIO.environment[TokenStorage]

      def _authorize = _settings.flatMap { implicit s =>
        InitialTokensService
          .execute(account)
          .foldM( 
            err => err match {
              case AuthError(AuthError.ErrorCodes.AuthorizationExpired.error, _, _) => zio.IO.succeed(AuthStatus.RegistrationExpired)
              case e => zio.IO.fail(ClientError.fromServiceError(e))
            },
            _storeTokens
          )
      }

      
      def _storeTokens(r : TokensResponse) : ZIO[TokenStorage,ClientError,AuthStatus] =
        _storage
          .flatMap(_.tokenStorage.storeTokens(account, Tokens(None, Some(r.access_token), Some(r.refresh_token))))
          .map(_ => AuthStatus.Succeeded(r.expires_in))
          .catchAll { err =>
            zio.IO.fail {
              Logger[Live.type].error(s"Cannot use API client; unexpected token storage failure on save operation: $err") 
              ClientError.ConfigurationError
            } 
          }

      def _tokenTask : ZIO[TokenStorage,ClientError,AuthStatus] =  
        _storage
          .flatMap(_.tokenStorage.getTokens(account))
          .map { toks => 
            if (toks.accessToken.isDefined && toks.refreshToken.isDefined) AuthStatus.AlreadyAuthorized
            else if (toks.authorizationToken.isDefined) AuthStatus.WaitingForAuthorization
            else AuthStatus.NeedsAuthorization
          }
          .catchAll {
            case TokenStorageError.InvalidAccountError => zio.UIO.succeed(AuthStatus.NeedsAuthorization)
            case err => zio.IO.fail {
              Logger[Live.type].error(s"Cannot use API client; token storage failure: $err") 
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



    def readThermostat(account : AccountID, id : ThermostatID) : ZIO[ClientEnv,ClientError,ThermostatStats] = {
      ZIO.access[ClientSettings](_.settings).flatMap { implicit s =>
        ThermostatService
          .execute(account, Select(SelectType.Thermostats(id.id), includeRuntime=true))
          .mapError(ClientError.fromServiceError)
          .flatMap { res =>
            zio.IO.fromEither {
              val tempOpt = for {
                t <- res.thermostatList.headOption
                r <- t.runtime
              } yield {
                val temp = Temperature(r.rawTemperature)
                ThermostatStats(t.name, temp.C, temp.F)
              }

              tempOpt.map(t => Right(t)).getOrElse(Left(ClientError.ThermostatNotFound))
            }
        }
      }
    }



    def readThermostats(account : AccountID) : ZIO[ClientEnv,ClientError,Iterable[ThermostatStats]] = {
      ZIO.access[ClientSettings](_.settings).flatMap { implicit s =>
        ThermostatService
          .execute(account, Select(SelectType.Registered, includeRuntime=true))
          .mapError(ClientError.fromServiceError)
          .map(_.thermostatList.flatMap(t => t.runtime.map { r => 
            val temp = Temperature(r.rawTemperature)
            ThermostatStats(t.name, temp.C, temp.F)
          })) 
      }
    }



    def register(account : AccountID): ZIO[ClientEnv,ClientError,Registration] = { 
      val svcTask = ZIO.access[ClientSettings](_.settings).flatMap { implicit s =>
        PinService
          .execute
          .mapError(ClientError.fromServiceError)
      }

      for {
        t <- svcTask
        _ <- ZIO.accessM[TokenStorage] { ts =>
              ts.tokenStorage
                .storeTokens(account, Tokens(Some(t.code), None, None))
                .catchAll { case tse =>
                  Logger[Live.type].error(s"Cannot use API client; token storage failure: $tse")
                  zio.IO.fail(ClientError.ConfigurationError)
                }
            }
      } yield Registration(t.ecobeePin, t.expires_in, t.interval)
    }
  }



  /** Factory for default implementation of the Ecobee client API, [[Default]]. */ 
  object Default {
    def newClient = zio.ZIO.access[ClientEnv] { env => new Default(env) }
  }

  /** Default implementation of the Ecobee client API, build on [[Live]].
    * 
    * @param env The `ClientEnv` environment provided to `Live`.
    * 
    * @note To create a new implementation, use [[Default$.newClient]]
    */ 
  final class Default private (env : ClientEnv) extends ApiClient {
    val apiClient = new Service[Any] {
      import scala.language.implicitConversions
      implicit def p[E,A](a : ZIO[ClientEnv,E,A]) : ZIO[Any,E,A] = a.provide(env)

      def authorize(account: AccountID): ZIO[Any,ClientError,AuthStatus] = Live.authorize(account)
      def readThermostat(account: AccountID, id: ThermostatID): ZIO[Any,ClientError,ThermostatStats] = Live.readThermostat(account, id)
      def readThermostats(account: AccountID): ZIO[Any,ClientError,Iterable[ThermostatStats]] = Live.readThermostats(account)
      def register(account: AccountID): ZIO[Any,ClientError,Registration] = Live.register(account)
    }
  }
}