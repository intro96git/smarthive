package com.kelvaya.ecobee.test.client

import com.kelvaya.ecobee.client.AccountID

import com.kelvaya.ecobee.client.tokens._
import zio._


object TestStorage extends TestConstants {
  def apply(account: AccountID) = new TestStorage {
    lazy val testStorageParams : Map[AccountID, Tokens] = Map(account -> Tokens(Some(AuthCode), Some(AccessToken), Some(RefreshToken)))
  }  
}

trait TestStorage extends TokenStorage {
  val testStorageParams : Map[AccountID, Tokens]

  val tokenStorage = new TokenStorage.Service[Any] {
    private var _tokens = testStorageParams

    // May throw exception...fix it!!
    def getTokens(account : AccountID): IO[TokenStorageError,Tokens] = {
      val curr = _tokens.get(account)
      IO.fromEither(curr.map(Right(_)).getOrElse(Left(TokenStorageError.InvalidAccountError)))
    }

    // This won't handle parallelism or re-use.  Fix it!
    def storeTokens(account : AccountID, tokens: Tokens): UIO[Unit] = UIO { _tokens = _tokens + ((account, tokens)) }
  }
}
