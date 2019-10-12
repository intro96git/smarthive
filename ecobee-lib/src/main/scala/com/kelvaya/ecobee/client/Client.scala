package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.storage._
import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import cats.Monad
import monix.eval.Task
import spray.json.JsonFormat


/** API client which tracks authorization tokens and an Application key.
  *
  * @param storage The backend which stores all authorization tokens
  * @param settings (implicit) The application global settings  (from dependency injection, `DI`)
  * @param system (implicit) Akka Actor system for the HTTP service  (from dependency injection, `DI`)
  * @param async (implicit) Monad typeclass instance used to contain responses from service and storage calls.  (from dependency injection, `DI`)
  *
  * @tparam M The tagless final type that will hold results  (from dependency injection, `DI`)
  */
abstract class BaseClient[M[_]] (storage : TokenStorage[M])(implicit settings: Settings, system: ActorSystem, async : Monad[M]) extends RequestExecutor[M] {

  /** Application key used to authorize the application against the Ecobee API
    *
    * @note This must be grabbed individually by each application using this library
    * and the key is, by default, read in the [[Settings#EcobeeAppKey]] field.
    */
  def getAppKey = settings.EcobeeAppKey

  /** Access Token used to authorize a request against the API */
  def getAccessToken: EitherM[RequestError,String] = readFromStorage(_.accessToken)

  /** Authorization code used to initially authorize an installation of the application */
  def getAuthCode: EitherM[RequestError,String] = readFromStorage(_.authorizationToken)

  /** Refresh Token used to generate a new Access Token */
  def getRefreshToken: EitherM[RequestError,String] = readFromStorage(_.refreshToken)


  /** Returns a new client containing the given tokens stored on the backend [[TokenStorage]] store.
    *
    * @param accessToken New access token to be stored
    * @param refreshToken New refresh token to be stored
    */
  def storeTokens(accessToken : String, refreshToken : String) : EitherM[RequestError,Unit] = {

    import cats.Monad.ops._
    storage.getTokens.flatMap { 
      case Left(e)       => async.pure(Left(RequestError.TokenAccessError(e)))
      case Right(tokens) => {
        tokens.copy(accessToken  = Some(accessToken), refreshToken = Some(refreshToken))
        storage.storeTokens(tokens).map {
          case Left(e)  => Left(RequestError.TokenAccessError(e))
          case Right(v) => Right(v)
        }
      }
    }
  }


  /** Returns a new client containing the auth code stored on the backend [[TokenStorage]] store. */
  def storeAuthCode(authCode : String) : EitherM[TokenStorageError,Unit] = {
    async.flatMap(storage.getTokens) { 
      case Left(e)       => async.pure(Left(e))
      case Right(tokens) => {
        val newTokens = tokens.copy(authorizationToken = Some(authCode))
        storage.storeTokens(newTokens)
      }
    }
  }

  private def readFromStorage[S](f : Tokens => Option[S]) : EitherM[RequestError,S] = {
    async.map(storage.getTokens) {  tokens =>
      val fnValue = tokens.flatMap { 
        f(_) match {
          case None        => Left(TokenStorageError.MissingTokenError)
          case Some(value) => Right(value)
        }
      }
      fnValue match {
        case Left(e)  => Left(RequestError.TokenAccessError(e))
        case Right(v) => Right(v)
      }
    }
  }
}



/** Ecobee REST API client
  *
  * @param storage The backend which stores all authorization tokens
  * @param settings (implicit) The application global settings  (from dependency injection, `DI`)
  * @param system (implicit) Akka Actor system for the HTTP service  (from dependency injection, `DI`)
  * @param container (implicit) Monad typeclass instance used to contain responses from service and storage calls.  (from dependency injection, `DI`)
  *
  * @tparam M The monad container type that will hold results  (from dependency injection, `DI`)
  *
  * @see [[com.kelvaya.ecobee.client client]]
  */
final class Client[M[_]] (storage : TokenStorage[M])(implicit settings: Settings, system: ActorSystem, container : Monad[M]) extends BaseClient(storage) {
  private implicit val _materializer = ActorMaterializer()
  private implicit val _ec = system.dispatcher



  private lazy val _serverRoot = settings.EcobeeServerRoot

  def executeRequest[S : JsonFormat](req: EitherM[RequestError, Task[HttpRequest]]): EitherM[ServiceError, S] = ???
}
