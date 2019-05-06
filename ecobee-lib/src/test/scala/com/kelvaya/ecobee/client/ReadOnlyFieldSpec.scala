package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.test.BaseTestSpec

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri

class ReadOnlyFieldSpec extends BaseTestSpec {

  import ReadOnlyFieldSpec._

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val authFactory = this.createTestExecutor(Map.empty)


  "Read-only fields" must "take sensible defaults" in {
    val default = SimpleBody("hello", "world")
    val explicit = SimpleBody("hello", ReadOnly("world"))

    default shouldBe explicit
  }

  they must "not be set in a POST request" in {

  }

}

object ReadOnlyFieldSpec {
  case class SimpleBody(writable : String, readonly : ReadOnly[String])


  class SimpleGetRequest(body : SimpleBody)(implicit e : RequestExecutor, s : Settings, lb : LoggingBus) extends Request {
    val uri = Uri.Path.Empty
    val query = List.empty[Querystrings.Entry]
    val entity = Some(Right(body))

    // I don't think there's any way to easily detect, at compile time, that the read-onlys have not been set
  }


  class SimplePostRequest(body : SimpleBody)(implicit e : RequestExecutor, s : Settings, lb : LoggingBus) extends PostRequest {
    val uri = Uri.Path.Empty
    val query = List.empty[Querystrings.Entry]
    val entity = Some(Right(???))
  }
}
