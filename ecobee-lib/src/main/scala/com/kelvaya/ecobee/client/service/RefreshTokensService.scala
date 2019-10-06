package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

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
