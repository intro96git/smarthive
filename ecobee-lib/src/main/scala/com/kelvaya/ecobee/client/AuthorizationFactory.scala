package com.kelvaya.ecobee.client

import akka.http.scaladsl.model.headers.HttpCredentials
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.headers.Authorization

trait AuthorizationFactory {
  def generateAuthorizationHeader: Authorization
}
