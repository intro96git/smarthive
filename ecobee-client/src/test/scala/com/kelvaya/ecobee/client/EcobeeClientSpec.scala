package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.client.service.PinResponse
import com.kelvaya.ecobee.client.tokens.{Tokens,TokenStorage,TokenStorageError}
import com.kelvaya.ecobee.test.client.BaseTestSpec
import com.kelvaya.ecobee.test.client.TestConstants
import com.kelvaya.ecobee.test.client.TestClientSettings
import com.kelvaya.ecobee.test.client.ZioTest

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import akka.stream.ActorMaterializer

import com.twitter.finagle.http.{Response => HttpResponse}

import zio.IO
import zio.UIO
import zio.ZIO

import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

import spray.json._
import org.scalatest.exceptions.TestFailedException



case class Response(status : Status)

class EcobeeClientSpec extends BaseTestSpec with ZioTest with BeforeAndAfterAll {
  import EcobeeClientSpec._

  // ###################
  // Start and stop a test REST web service (see startTestServer at the botttom of the file)
  // ###################
  private implicit val _sys = ActorSystem("ecobee-client-test")
  private val _server: Future[Http.ServerBinding] = startTestServer

  override def beforeAll(): Unit = {
    val _ = Await.ready(_server, 5.seconds)
  }

  override def afterAll(): Unit = {
    implicit val ec = _sys.dispatcher
    val fut = for {
      sc <- _server
      _  <- sc.terminate(5.seconds)
      t  <- _sys.terminate()
    } yield t
    
    val _ = Await.ready(fut, 5.seconds)
  }
  // ###################
  
  
  // ###################
  // Request Executor helper functions to expose errors easier
  // ###################
  private def apiFn(t : Throwable, r : Option[HttpResponse]) = r.map(p=>ApiError(Status(-1, s"[${t.getClass.getName}] $p"))).getOrElse(ApiError(Status(-1, t.getClass.getName)))
  private def authFn(t : Throwable, r : Option[HttpResponse]) = r.map(p=>AuthError(t.getClass.getName,p.toString,"")).getOrElse(AuthError(t.getClass.getName,"WTF",""))
  // ###################

  
  // Serialization implicit for fake HTTP responses as given by the test REST web service
  implicit val ResponseFormat = DefaultJsonProtocol.jsonFormat1(Response)


  // ClientSettings pointing to the test REST web service
  implicit object LocalServerSettings extends TestClientSettings.TestClientService {
    override lazy val EcobeeServerRoot = new java.net.URL("http://localhost:6789")
  }

  val Settings = new TestClientSettings {
    override val settings: TestClientSettings.TestClientService = LocalServerSettings
  }
  
  // ##################################################################################################################
  // ##################################################################################################################

  "The request executor" must "handle successful HTTP communication" in {

    val request1 = new TestApiRequest(this.account, "/test1")
    val request2 = new TestAuthRequest(this.account, "/auth1")

    val requestExecutor = RequestExecutorImpl.create.map(_.requestExecutor).provide(Settings)
      
    val test1 = for {
      exec <- requestExecutor
      req  <- request1.createRequest.provide(new EcobeeClientSpec.MemoryTokenStorage)
      rsp  <- exec.executeRequest[ApiError,Response](req, _.convertTo[ApiError], apiFn(_,_))
      a    <- rsp shouldBe Response(Status(0, "Your request was successfully received and processed."))
    } yield a
    
    val test2 = for {
      exec <- requestExecutor
      req <- request2.createRequest.provide(new EcobeeClientSpec.MemoryTokenStorage)
      rsp <- exec.executeRequest[AuthError,PinResponse](req, _.convertTo[AuthError], authFn(_,_))
      a   <- rsp shouldBe PinResponse("bv29", 9, "uiNQok9Uhy5iScG4gncCAilcFUMK0zWT", PinScope.SmartWrite, 30)
    } yield a


    
    run(test1 *> test2)
  }

  it must "be able to handle failed web service calls" in {
    val request1 = new TestApiRequest(this.account, "/test2")
    val request2 = new TestAuthRequest(this.account, "/auth2")
    
    val requestExecutor = RequestExecutorImpl.create.map(_.requestExecutor).provide(Settings)
    
    val test1 = for {
      exec <- requestExecutor
      req  <- request1.createRequest.provide(new EcobeeClientSpec.MemoryTokenStorage)
      rsp  <- 
        exec
          .executeRequest[ApiError,Response](req, _.convertTo[ApiError], apiFn(_,_))
          .map(r => new TestFailedException(s"Request did not fail.  Response: $r", new RuntimeException, 1))
          .flip
      a    <- rsp shouldBe ApiError(Status(1, "Invalid credentials supplied to the registration request, or invalid token. Request registration again."))
    } yield a
    
    val test2 = for {
      exec <- requestExecutor
      req  <- request2.createRequest.provide(new EcobeeClientSpec.MemoryTokenStorage)
      rsp  <- 
        exec
          .executeRequest[AuthError,Response](req, _.convertTo[AuthError], authFn(_,_))
          .map(r => new TestFailedException(s"Request did not fail.  Response: $r", new RuntimeException, 1))
          .flip
      a    <- rsp shouldBe AuthError("access_denied", "Authorization has been denied by the user. This is only used in the Authorization Code authorization browser redirect.", "https://example.org")
    } yield a

    
    run(test1 *> test2)
  }
}

// ##################################################################################################################
// ##################################################################################################################

object EcobeeClientSpec extends TestConstants {

  // Mock token storage.  Not used, but needed for compilation
  class MemoryTokenStorage (t : Map[AccountID,Tokens] = Map(BaseTestSpec.DefaultAccount -> Tokens(Some(AuthCode),Some(AccessToken),Some(RefreshToken)))) extends TokenStorage {
    private val _tokens = new scala.collection.mutable.HashMap ++ t
    
    val tokenStorage = new TokenStorage.Service[Any] {
      def getTokens(account: AccountID): ZIO[Any,TokenStorageError,Tokens] = IO.fromEither {
        _tokens.get(account) match {
          case Some(tokens) => Right(tokens)
          case _            => Left(TokenStorageError.InvalidAccountError)
        }
      }

      def storeTokens(account: AccountID,tokens: Tokens): ZIO[Any,TokenStorageError,Unit] = IO.fromEither(Right(_tokens.update(account, tokens)))
    }
  }


  // Starts the Akka HTTP web service
  private def startTestServer(implicit as : ActorSystem) = {
    implicit val ma = ActorMaterializer()
    implicit val ec = as.dispatcher

    val route = concat(
      path("1" / "test1") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`,"""{ "status" : { "code":0, "message":"Your request was successfully received and processed." } }"""))
        }
      },
      path("1" / "test2") {
        get {
          complete(StatusCodes.InternalServerError -> HttpEntity(ContentTypes.`application/json`,"""{ 
            "status" : { 
              "code":1, 
              "message":"Invalid credentials supplied to the registration request, or invalid token. Request registration again." 
            } 
          }"""))
        }
      },
      path("auth1") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`,"""{
            "ecobeePin": "bv29",
            "code": "uiNQok9Uhy5iScG4gncCAilcFUMK0zWT",
            "scope": "smartWrite",
            "expires_in": 9,
            "interval": 30
          }"""))
        }
      },
      path("auth2") {
        get {
          complete(StatusCodes.BadGateway -> HttpEntity(ContentTypes.`application/json`,"""{ 
            "error" : "access_denied",
            "error_description" : "Authorization has been denied by the user. This is only used in the Authorization Code authorization browser redirect.",
            "error_uri" : "https://example.org"
          }"""))
        }
      }
    )

    Http().bindAndHandle(route, "localhost", 6789)
  }

  private class TestApiRequest(val account : AccountID, path : String)(implicit s : ClientSettings.Service[Any]) extends RequestNoEntity with AuthorizedRequest[ParameterlessApi] {
    val query: TokenStorage.IO[List[Querystrings.Entry]] = UIO(List.empty)
    val queryBody = UIO(None)
    val uri: Uri = Uri(path)
  }
  
  private class TestAuthRequest(val account : AccountID, path : String)(implicit s : ClientSettings.Service[Any]) extends RequestNoEntity {
    val query: TokenStorage.IO[List[Querystrings.Entry]] = UIO(List.empty)
    val queryBody = UIO(None)
    val uri: Uri = Uri(path)
  }
}
