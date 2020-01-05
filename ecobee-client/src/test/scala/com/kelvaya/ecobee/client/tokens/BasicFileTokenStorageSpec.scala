package com.kelvaya.ecobee.client.tokens

import com.kelvaya.ecobee.test.client._

import better.files.File

import zio.Task
import zio.UIO
import org.scalatest.compatible.Assertion

class BasicFileTokenStorageSpec extends BaseTestSpec with TokenStorageBehavior {

  private val InitialFile = s"""
    {
      "tokens" : [
        { "client" : "test", "tokens" : { "authorizationToken" : "$AuthCode", "accessToken" : "$AccessToken", "refreshToken" : "$RefreshToken" } }
      ]
    }
  """


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


  it must "fail with a ConnectionError if the file does not exist" in {
    val test = Task(File.newTemporaryFile(prefix="btfs")).bracket(cleanup)  { file =>
      file.append("GARBAGE")
      
      for {
        _ <- BasicFileTokenStorage.connect(file)
      } yield (fail("Creating token storage on a file with bad data should not succeed"))
    }
    
    this.runtime.unsafeRun(test.either) shouldBe Left(TokenStorageError.ConnectionError)
  }


  it must "fail with a ConnectionError is the file contains malformed data" in {
    val test = for {
      _ <- BasicFileTokenStorage.connect(File("foobar.bad"))
    } yield (fail("Creating token storage on a bad file should not succeed"))
    
    this.runtime.unsafeRun(test.either) shouldBe Left(TokenStorageError.ConnectionError)
  }


  // Provides an auto-created-and-deleted BasicFileTokenStorage to the given function
  // Is used as the function for the TokenStorageBehavior tests.
  private def usingNewFile(actions : TokenStorage.Service[Any] => Task[Assertion]) : Task[Assertion] = {    
    
    Task(File.newTemporaryFile(prefix="btfs")).bracket(cleanup)  { file =>      
      file.append(InitialFile)
      BasicFileTokenStorage.connect(file).flatMap { st =>
        val store = st.tokenStorage
        actions(store)
      }
    }

  }


  def cleanup(f : File) = UIO(f.delete())

}