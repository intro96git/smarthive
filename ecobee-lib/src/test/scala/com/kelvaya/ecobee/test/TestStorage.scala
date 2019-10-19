package com.kelvaya.ecobee.test

import com.kelvaya.ecobee.client.AccountID

import com.kelvaya.ecobee.client.storage._

import cats.Id
import cats.Monad

object TestStorage extends TestConstants {
  def apply(account: AccountID) = new TestStorage(Map(account -> Tokens(Some(AuthCode), Some(AccessToken), Some(RefreshToken))))
}

class TestStorage private (store : Map[AccountID,Tokens]) extends TokenStorage[Id] {
  type Self = TestStorage

    private var _tokens = store

    private def pure[A] : A => Id[A] = implicitly[Monad[Id]].pure _

    // May throw exception...fix it!!
    def getTokens(account : AccountID): Id[Either[TokenStorageError,Tokens]] = pure(Right(_tokens(account)))

    // This won't handle parallelism or re-use.  Fix it!
    def storeTokens(account : AccountID, tokens: Tokens): Id[Either[TokenStorageError,Unit]] = { _tokens = _tokens + ((account, tokens)); pure(Right(()))}

    val close : Id[Either[TokenStorageError,Unit]] = pure(Right(()))
}
