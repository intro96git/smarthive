package com.kelvaya.ecobee.client

import cats.Monad

import scala.language.higherKinds

class RequestExecutorImpl[M[_] : Monad] extends RequestExecutor[M] {
  def executeRequest[S](req: cats.data.EitherT[M,com.kelvaya.ecobee.client.RequestError,monix.eval.Task[akka.http.scaladsl.model.HttpRequest]])(implicit evidence$1: spray.json.JsonFormat[S]): cats.data.EitherT[M,com.kelvaya.ecobee.client.service.ServiceError,S] = ???
  def getAccessToken: cats.data.OptionT[M,String] = ???
  def getAppKey: String = ???
  def getAuthCode: cats.data.OptionT[M,String] = ???
  def getRefreshToken: cats.data.OptionT[M,String] = ???
}