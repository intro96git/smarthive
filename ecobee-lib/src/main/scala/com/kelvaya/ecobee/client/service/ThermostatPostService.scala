package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.ThermostatModification
import com.kelvaya.ecobee.client.WriteableApiObject
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.marshallers._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.DefaultJsonProtocol._

object ThermostatPostRequest {

  /** Endpoint of this POST request (/thermostat) */
  private val Endpoint = Uri.Path("/thermostat")

  /** The JSON body of a [[ThermostatPostRequest]] */
  case class RequestBody private (selection : Select, thermostat : Option[ThermostatModification], functions : Option[Seq[ThermostatFunction]]) extends WriteableApiObject
  implicit def requestBodyFormat(implicit ev : LoggingBus) = DefaultJsonProtocol.jsonFormat3(RequestBody)
}

/** Request to modify an Ecobee [[Thermostat]]
  *
  * This is used by [[ThermostatPostService]] when communicating with the Ecobee service.
  *
  * @note One may also use the overloaded service` method of `ThermostatPostService` instead of creating a `ThermostatPostRequest`.
  *
  * @param selection The selection criteria for update
  * @param thermostat The thermostat and fields to modify.  Send only the minimum to be modified.
  * @param functions Any thermostat functions to execute after performing the modifications on `#thermostat`.
  */
case class ThermostatPostRequest(selection : Select, thermostat : Option[ThermostatModification], functions : Option[Seq[ThermostatFunction]])(implicit e : RequestExecutor, s : Settings, log : LoggingBus) extends Request[ThermostatPostRequest.RequestBody]
  with PostRequest[ThermostatPostRequest.RequestBody] {

  import ThermostatPostRequest._

  val entity = Some(RequestBody(selection, thermostat, functions))
  val query = List.empty[Querystrings.Entry]
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
  * @param ev The AKKA `LoggingBus` that can record application log messages
  */
class ThermostatPostService(implicit ev : LoggingBus) extends EcobeeJsonService[ThermostatPostRequest, ThermostatPostResponse] {
  def execute[R[_]](selectType : SelectType, thermostat : Option[ThermostatModification] = None, functions : Option[Seq[ThermostatFunction]] = None)(implicit r : Realizer[R], c : Client, e : RequestExecutor, s : Settings) : R[Either[ServiceError, ThermostatPostResponse]] =
    execute(ThermostatPostRequest(Select(selectType), thermostat, functions))
}
