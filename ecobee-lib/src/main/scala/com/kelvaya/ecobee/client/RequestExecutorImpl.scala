package com.kelvaya.ecobee.client

import cats.Monad

import scala.language.higherKinds

class RequestExecutorImpl[M[_] : Monad] extends RequestExecutor[M] {
/** As seen from class RequestExecutorImpl, the missing signatures are as follows.
 *  For convenience, these are usable as stub implementations.
 */
  def executeRequest[S](req: M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,monix.eval.Task[akka.http.scaladsl.model.HttpRequest]]])(implicit evidence$1: spray.json.JsonFormat[S]): M[scala.util.Either[com.kelvaya.ecobee.client.service.ServiceError,S]] = ???
  def getAccessToken: M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???
  def getAppKey: String = ???
  def getAuthCode: M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???
  def getRefreshToken: M[scala.util.Either[com.kelvaya.ecobee.client.RequestError,String]] = ???

}