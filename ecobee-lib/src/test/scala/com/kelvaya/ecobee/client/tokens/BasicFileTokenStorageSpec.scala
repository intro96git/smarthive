package com.kelvaya.ecobee.client.storage

import com.kelvaya.ecobee.test._
import com.kelvaya.ecobee.client.tokens.BasicFileTokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorage
import com.kelvaya.ecobee.client.tokens.TokenStorageBehavior

import better.files.File

import zio.Task
import zio.UIO
import org.scalatest.compatible.Assertion
import com.kelvaya.ecobee.client.tokens.Tokens

class BasicFileTokenStorageSpec extends BaseTestSpec with TokenStorageBehavior {

  private val InitialFile = s"""
    {
      "tokens" : [
        { "client" : "test", "tokens" : { "authorizationToken" : "$AuthCode", "accessToken" : "$AccessToken", "refreshToken" : "$RefreshToken" } }
      ]
    }
  """

  import deps.Implicits._


  "The json-based file token storage driver" must behave like storage(usingNewFile)

  it must "properly store the tokens to a file" in {
    val test = Task(File.newTemporaryFile(prefix="btfs")).bracket(cleanup)  { file =>
      file.append(InitialFile)
      
      val step1 = for {
        store  <- BasicFileTokenStorage.connect(file)
        _      <- store.tokenStorage.storeTokens(this.account, Tokens(Some("test1"), Some("test2"), Some("test3")))
      } yield (())

      
      val step2 = for {
        store  <- BasicFileTokenStorage.connect(file)
        tokens <- store.tokenStorage.getTokens(this.account)
        a      <- tokens shouldBe Tokens(Some("test1"), Some("test2"), Some("test3"))
      } yield a


      val step3 = Task {
        import spray.json._
        file.contentAsString.parseJson shouldBe """
        {
          "tokens" : [
            { "client" : "test", "tokens" : { "authorizationToken" : "test1", "accessToken" : "test2", "refreshToken" : "test3" } }
          ]
        }
        """.parseJson
      }

      step1 *> step2 *> step3
    }
    
    this.run(test)
  }


  // Provides an auto-created-and-deleted BasicFileTokenStorage to the given function
  // Is used as the function for the TokenStorageBehavior tests.
  private def usingNewFile(actions : TokenStorage.Service[Any] => Task[Assertion]) : Task[Assertion] = {    
    
    Task(File.newTemporaryFile(prefix="btfs")).bracket(cleanup)  { file =>      
      file.append(InitialFile)
      BasicFileTokenStorage.connect(file).flatMap { st =>
        val store = st.tokenStorage
        try actions(store)
        finally { store.close() ; () }
      }
    }

  }


  def cleanup(f : File) = UIO(f.delete())

}