package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.test.BaseTestSpec
import zio.Task
import com.kelvaya.ecobee.test.ZioTest
import org.scalatest.compatible.Assertion
import com.kelvaya.ecobee.client.AccountID

trait TokenStorageBehavior extends BaseTestSpec with ZioTest {
  
  
  def storage(connect : (TokenStorage.Service[Any] => Task[Assertion]) => Task[Assertion]) = {

    it must "return an InvalidAccountError if the account does not exist" in {
      val test = connect { store =>
        for {
          bad <- store.getTokens(new AccountID("bogus")).either
          a   <- bad shouldBe Left(TokenStorageError.InvalidAccountError)
        } yield a
      }

      run(test)
    }

    it must "be able to read and write the authorization code for a thermostat" in {

      val test = connect { store => 
        for {
          tokens   <- store.getTokens(this.account)
          _        <- tokens.authorizationToken shouldBe Some(AuthCode)
          newTokens = tokens.copy(authorizationToken = Some("12345"))
          _        <- store.storeTokens(this.account, newTokens)
          up       <- store.getTokens(this.account)
          a        <- up.authorizationToken shouldBe Some("12345")
        } yield a
      }

      run(test)      
    }

    it must "be able to read and write the access token for a thermostat" in {
      
      val test = connect { store => 
        for {
          tokens   <- store.getTokens(this.account)
          _        <- tokens.accessToken shouldBe Some(AccessToken)
          newTokens = tokens.copy(accessToken = Some("12345"))
          _        <- store.storeTokens(this.account, newTokens)
          up       <- store.getTokens(this.account)
          a        <- up.accessToken shouldBe Some("12345")
        } yield a
      }

      run(test)      

    }

    it must "be able to read and write the refresh token for a thermostat" in {
      
      val test = connect { store => 
        for {
          tokens   <- store.getTokens(this.account)
          _        <- tokens.refreshToken shouldBe Some(RefreshToken)
          newTokens = tokens.copy(refreshToken = Some("12345"))
          _        <- store.storeTokens(this.account, newTokens)
          up       <- store.getTokens(this.account)
          a        <- up.refreshToken shouldBe Some("12345")
        } yield a
      }

      run(test)      
    }
  }
}
