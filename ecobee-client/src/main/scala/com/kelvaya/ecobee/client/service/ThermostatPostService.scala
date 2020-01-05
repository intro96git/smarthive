package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.AuthorizedRequest
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.ThermostatModification
import com.kelvaya.ecobee.client.WriteableApiObject
import com.kelvaya.ecobee.client.ClientSettings

import com.typesafe.scalalogging.Logger

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri

import spray.json._
import spray.json.DefaultJsonProtocol._

import zio.UIO
import zio.ZIO


/** Contains constants and supporting case classes for [[ThermostatPostRequest]] */
object ThermostatPostRequest {

  /** Endpoint of this POST request (/thermostat) */
  private val Endpoint = Uri.Path("/thermostat")

  /** The JSON body of a [[ThermostatPostRequest]] */
  case class RequestBody private (selection : Select, thermostat : Option[ThermostatModification], functions : Option[Seq[ThermostatFunction]]) extends WriteableApiObject
  implicit def requestBodyFormat(implicit ev : Logger) = DefaultJsonProtocol.jsonFormat3(RequestBody)
}

/** Request to modify an Ecobee [[Thermostat]]
  *
  * This is used by [[ThermostatPostService]] when communicating with the Ecobee service.
  *
  * @note One may also use the overloaded service` method of `ThermostatPostService` instead of creating a `ThermostatPostRequest`.
  *
  * @param account The ID of the account for which the token request will be made
  * @param selection The selection criteria for update
  * @param thermostat The thermostat and fields to modify.  Send only the minimum to be modified.
  * @param functions Any thermostat functions to execute after performing the modifications on `#thermostat`.
  */
case class ThermostatPostRequest(
  override val account: AccountID, 
  selection : Select, 
  thermostat : Option[ThermostatModification], 
  functions : Option[Seq[ThermostatFunction]])(implicit s : ClientSettings.Service[Any], log : Logger)
extends Request[ThermostatPostRequest.RequestBody](account)
with PostRequest[ThermostatPostRequest.RequestBody]
with AuthorizedRequest[ThermostatPostRequest.RequestBody]
{

  import ThermostatPostRequest._

  val entity = Some(RequestBody(selection, thermostat, functions))
  val query = UIO(List.empty[Querystrings.Entry])
  val uri : Uri.Path = Endpoint
}


// ################################################################################################################
// ################################################################################################################


/** Response to a request to modify an Ecobee [[Thermostat]]
  *
  * This is used by [[ThermostatPostService]] when communicating with the Ecobee service.
  *
  * @param status The Ecobee API response code
  */
case class ThermostatPostResponse(status : Status)
object ThermostatPostResponse {
  implicit val Format = DefaultJsonProtocol.jsonFormat1(ThermostatPostResponse.apply)
}

// ################################################################################################################
// ################################################################################################################

/** Service to update an Ecobee [[Thermostat]].
  *
  * Requires a [[ThermostatPostRequest]] and the API responds with a [[ThermostatPostResponse]]
  *
  * @param ev The AKKA `Logger` that can record application log messages
  */
class ThermostatPostService(implicit ev : Logger) extends EcobeeJsonService[ThermostatPostRequest,ThermostatPostResponse] {

  /** Execute the request against the API, returning the [[ThermostatPostResponse]]
    *
    * @param account The ID of the account for which the token request will be made
    * @param selection The selection criteria for update
    * @param thermostat The thermostat and fields to modify.  Send only the minimum to be modified.
    * @param functions Any thermostat functions to execute after performing the modifications on `#thermostat`.
    */ 
  def execute(
    account: AccountID, 
    selectType : SelectType, 
    thermostat : Option[ThermostatModification] = None, 
    functions : Option[Seq[ThermostatFunction]] = None
  )(implicit s : ClientSettings.Service[Any]) : ZIO[ClientEnv, ServiceError, ThermostatPostResponse] = 
    execute(ThermostatPostRequest(account, Select(selectType), thermostat, functions))
}
