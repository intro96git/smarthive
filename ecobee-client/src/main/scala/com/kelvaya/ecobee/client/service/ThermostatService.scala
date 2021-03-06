package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.AuthorizedRequest
import com.kelvaya.ecobee.client.ClientSettings
import com.kelvaya.ecobee.client.Page
import com.kelvaya.ecobee.client.ParameterlessApi
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.Thermostat
import com.kelvaya.ecobee.client.Uri

import spray.json._
import spray.json.DefaultJsonProtocol._

import zio.UIO
import zio.ZIO

/** Factories for [[ThermostatRequest]] */
object ThermostatRequest {
  private val Endpoint = Uri("/thermostat")

  /** Returns new [[ThermostatRequest]] to initially request information.
    *
    * @param account The ID of the account for which the thermostat request will be made
    * @param selection The selection criteria for the thermostat query
    * @param s (implicit) The application global settings
    */
 def apply(account: AccountID, selection : Select)(implicit s : ClientSettings.Service[Any]) : ThermostatRequest = ThermostatRequest(account, selection, None)
  
  /** Returns new [[ThermostatRequest]] to request information from a starting page.
    *
    * @param account The ID of the account for which the thermostat request will be made
    * @param selection The selection criteria for the thermostat query
    * @param page The page of information to return
    * @param s (implicit) The application global settings
    */
 def apply(account: AccountID, selection : Select, page : Int)(implicit s : ClientSettings.Service[Any]) : ThermostatRequest = ThermostatRequest(account, selection, Some(page))
}


/** Request for [[Thermostat]] information
  * 
  * @param account The ID of the account for which the thermostat request will be made
  * @param selection The selection criteria for the thermostat query
  * @param page The page of information to return
  * @param s (implicit) The application global settings
  */
case class ThermostatRequest(val account: AccountID, selection : Select, page : Option[Int] = None)
(implicit s : ClientSettings.Service[Any]) extends RequestNoEntity with AuthorizedRequest[ParameterlessApi] {

  val pageQS : Option[JsField] = page map { p => new JsField("page", Page(Some(p), None, None, None).toJson ) }

  val queryBody: UIO[Option[String]] =  UIO {
    val selectJson = "selection" -> selection.toJson
    val jsonMap = (selectJson :: pageQS.map(p => List(p)).getOrElse(List.empty)).toMap
    val fullJson = JsObject(jsonMap)
    Some(fullJson.compactPrint)
  }
  val query = UIO.succeed(Nil)
  val uri = ThermostatRequest.Endpoint

}


// ############################################################
// ############################################################

/** Implicits for JSON serialization of [[ThermostatResponse]] */
object ThermostatResponse {
  implicit val ResponseFormat = DefaultJsonProtocol.jsonFormat3(ThermostatResponse.apply)
}


/** Response from a [[ThermostatRequest]]
  * 
  * @param theromostatList The list of theromstats
  * @param page The page of information on the thermostats
  * @param status The return status from the API
  */
case class ThermostatResponse(
    thermostatList : Seq[Thermostat],
    page :           Page,
    status :         Status
)


// ############################################################
// ############################################################



/** Service for requesting information on a [[Thermostat]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param selection The selection criteria for update
  * @param page The page of information to return
  * @param ev (implicit) The logger of the caller
  * @param s (implicit) The application global settings
  *
  * @example
{{{
  implicit val log : akka.event.Logger = ...
  implicit val settings : ClientSettings.Service[Any] = ...

  val thermResponse = ThermostatService.execute(account, selection)
}}}
  * @see [[ThermostatRequest]]
  */
object ThermostatService {


  /** Implicitly converts the object into a [[ThermostatService]] for the [[ThermostatRequest]] request
    *
    * This allows the syntax, `ThermostatService.execute`, to work instead of having to create both
    * an `ThermostatRequest` and pass it explicitly to a new `ThermostatServiceImpl`.
    * 
    * @param ev (implicit) The logger of the caller
    * @param s (implicit) The application global settings
    */
  implicit class ThermostatServiceImpl(o : ThermostatService.type)(implicit s : ClientSettings.Service[Any]) extends EcobeeJsonService[ThermostatRequest,ThermostatResponse] {

    def execute(account : AccountID, selection : Select): ZIO[ClientEnv, ServiceError, ThermostatResponse] =
      pexecute(account, selection, None)

    def execute(account : AccountID, selection : Select, page : Int): ZIO[ClientEnv, ServiceError, ThermostatResponse] =
      pexecute(account, selection, Some(page))

    private def pexecute(account : AccountID, selection : Select, page : Option[Int]): ZIO[ClientEnv, ServiceError, ThermostatResponse] =
      execute(ThermostatRequest(account, selection, page))
  }
}