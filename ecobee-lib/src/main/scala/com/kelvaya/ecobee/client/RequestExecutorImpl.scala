package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.ServiceError
import com.kelvaya.ecobee.client.tokens.TokenStorage

import spray.json.JsonFormat
import akka.http.scaladsl.model.HttpRequest

import zio.{IO,ZIO}

class RequestExecutorImpl extends RequestExecutor {
  def executeRequest[S:JsonFormat](req: ZIO[TokenStorage,RequestError,HttpRequest]) : IO[ServiceError,S] = ???
}