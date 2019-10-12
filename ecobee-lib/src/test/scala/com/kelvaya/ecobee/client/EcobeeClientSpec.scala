package com.kelvaya.ecobee.client

import scala.util.Random

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.storage.{Tokens,TokenStorage,TokenStorageError}
import com.kelvaya.ecobee.test.BaseTestSpec

import cats._


class EcobeeClientSpec extends BaseTestSpec {

  import deps.Implicits._
  import EcobeeClientSpec._


  "The API client" must "be able to store a returned authorization key from a PIN request" in {
    val storage = new MemoryTokenStorage
    val client = new Client(storage)
    val newPinValue = Random.alphanumeric.take(10).mkString
    
    client.getAuthCode(account) shouldBe Left(RequestError.TokenAccessError(TokenStorageError.MissingTokenError))
    storage.getTokens(account).authorizationToken shouldBe None

    client.storeAuthCode(account, newPinValue)

    client.getAuthCode(account) shouldBe Right(newPinValue)
    storage.getTokens(account).authorizationToken shouldBe Some(newPinValue)
  }

  it must "be able to store the access and refresh tokens from a token request" in {
    val storage = new MemoryTokenStorage
    val client = new Client(storage)
    val newAccessValue = Random.alphanumeric.take(10).mkString
    val newRefreshValue = Random.alphanumeric.take(10).mkString

    client.getAccessToken(account) shouldBe Left(RequestError.TokenAccessError(TokenStorageError.MissingTokenError))
    client.getRefreshToken(account) shouldBe Left(RequestError.TokenAccessError(TokenStorageError.MissingTokenError))
    storage.getTokens(account).accessToken shouldBe None
    storage.getTokens(account).refreshToken shouldBe None

    client.storeTokens(account, newAccessValue, newRefreshValue)
    client.getAccessToken(account) shouldBe Right(newAccessValue)
    client.getRefreshToken(account) shouldBe Right(newRefreshValue)
    storage.getTokens(account).accessToken shouldBe Some(newAccessValue)
    storage.getTokens(account).refreshToken shouldBe Some(newRefreshValue)
  }


  it must "be able to read the application key from settings" in {
    val expected = deps.settings.EcobeeAppKey

    val client = new Client(new MemoryTokenStorage())
    client.getAppKey shouldBe expected
  }

}

object EcobeeClientSpec {
  class MemoryTokenStorage (t : Map[AccountID,Tokens] = Map(BaseTestSpec.DefaultAccount -> Tokens(None,None,None))) extends TokenStorage[Id] {
    type Self = MemoryTokenStorage

    type TokenId[A] = Id[Either[TokenStorageError,A]]

    private val _tokens = new scala.collection.mutable.HashMap ++ t

    private def pure[A] : A => Id[A] = implicitly[Monad[Id]].pure _

    val close : TokenId[Unit] = pure(Right(()))

    def getTokens(account: AccountID): cats.Id[Either[TokenStorageError,Tokens]] = pure(Right(_tokens(account)))
    def storeTokens(account: AccountID,tokens: Tokens): cats.Id[Either[TokenStorageError,Unit]] = pure(Right(_tokens.update(account,tokens)))
    
  }
}
