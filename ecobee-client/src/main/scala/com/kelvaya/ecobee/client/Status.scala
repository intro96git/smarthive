package com.kelvaya.ecobee.client

import scala.collection.mutable
import spray.json._
import spray.json.DefaultJsonProtocol._


/** Implicit conversion for JSON serialization and enumeration of status codes */
object Status {
  implicit object StatusFormat extends RootJsonFormat[Status] {
    private val default = DefaultJsonProtocol.jsonFormat2(Status.apply)
    
    def read(json: JsValue): Status = {
      val tryStatus = default.read(json)
      Statuses.All.get(tryStatus.code).getOrElse(tryStatus)
    }
    
    def write(obj: Status): JsValue = default.write(obj)
  }
}

/** Status returned by the Ecobee API for a request 
  *
  * @see [[ApiError]]
  * @see [[Statuses$]] 
  */
final case class Status(code : Int, message : String) extends ReadonlyApiObject


// ###########################################################################################


/** Enumeration of [[Status]] messages that may be returned by the Ecobee API  */
object Statuses {
  private val _codeList = mutable.ListBuffer.empty[Status]
  private def create(c : Int, m : String) : Status = {
    val code = Status(c, m)
    _codeList += code
    code
  }

  val Success = create(0, "Success")
  val AuthFailed = create(1, "Authentication failed.")
  val NotAuthorized = create(2, "Not authorized.")
  val ProcessingError = create(3, "Processing error.")
  val SerializationError = create(4, "Serialization error.")
  val InvalidRequest = create(5, "Invalid request format.")
  val TooManyThermostats = create(6, "Too many thermostat in selection match criteria.")
  val ValidateError = create(7, "Validation error.")
  val InvalidFunc = create(8, "Invalid function.")
  val InvalidSelect = create(9, "Invalid selection.")
  val InvalidPage = create(10, "Invalid page.")
  val FuncError = create(11, "Function error.")
  val PostUnsupported = create(12, "Post not supported for request.")
  val GetUnsupported = create(13, "Get not supported for request.")
  val ExpiredToken = create(14, "Authentication token has expired. Refresh your tokens.")
  val DuplicationError = create(15, "Duplicate data violation.")
  val InvalidToken = create(16, "Invalid token. Token has been deauthorized by user. You must re-request authorization.")

  lazy val All = {
    _codeList.map(sc => (sc.code, sc) ).toMap
  }
}
