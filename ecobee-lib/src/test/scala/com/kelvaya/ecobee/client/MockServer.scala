package com.kelvaya.ecobee.client

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import com.kelvaya.ecobee.test.TestConstants

class MockServer {

}

object MockAuthorizationFactory extends AuthorizationFactory with TestConstants {
  def generateAuthorizationHeader = Authorization(OAuth2BearerToken(MockAuthToken))
}