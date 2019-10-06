package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.PinScope
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.util.Realizer
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.TokenType
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.Uri
import spray.json.DefaultJsonProtocol
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Querystrings.GrantType
import cats.Monad
import cats.data.OptionT

class RefreshTokensRequest[M[_]:Monad](implicit e : RequestExecutor[M], s : Settings) extends TokensRequest[M] {
  final def authTokenQs : OptionT[M,Querystrings.Entry] = OptionT.liftF(this.getRefreshTokenQs)
  final def grantTypeQs : Querystrings.Entry = GrantType.RefreshToken
}

// ---------------------

object RefreshTokensService {
  implicit class RefreshTokensServiceImpl[M[_]:Monad](o : RefreshTokensService.type) extends TokensService[M,RefreshTokensRequest[M]] {
    def newTokenRequest(implicit e : RequestExecutor[M], s : Settings) = new RefreshTokensRequest
  }
}
