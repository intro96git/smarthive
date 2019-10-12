package com.kelvaya.ecobee.client

import cats.Monad

import scala.language.higherKinds

class RequestExecutorImpl[M[_] : Monad] extends RequestExecutor[M] {

/** As seen from class RequestExecutorImpl, the missing signatures are as follows.
 *  For convenience, these are usable as stub implementations.
 */
  protected val account: com.kelvaya.ecobee.client.AccountID = ???
  def getAccessToken(account: com.kelvaya.ecobee.client.AccountID): M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???
  def getAuthCode(account: com.kelvaya.ecobee.client.AccountID): M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???
  def getRefreshToken(account: com.kelvaya.ecobee.client.AccountID): M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???
  def executeRequest[S](req: M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,monix.eval.Task[akka.http.scaladsl.model.HttpRequest]]])(implicit evidence$1: spray.json.JsonFormat[S]): M[scala.util.Either[com.kelvaya.ecobee.client.service.ServiceError,S]] = ???
  def getAppKey: String = ???

}