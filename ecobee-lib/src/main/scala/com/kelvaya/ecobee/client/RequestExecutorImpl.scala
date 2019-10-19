package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError

import cats.Monad
import spray.json.JsonFormat
import monix.eval.Task
import akka.http.scaladsl.model.HttpRequest

import scala.language.higherKinds

class RequestExecutorImpl[F[_] : Monad,M[_]] extends RequestExecutor[F,M] {
  def executeRequest[S:JsonFormat](req: Task[F[Either[RequestError,HttpRequest]]]) : M[Either[ServiceError,S]] = ???
}