package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.PostRequest
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.ThermostatModification
import com.kelvaya.ecobee.client.WriteableApiObject
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.storage.TokenStorage

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri

import spray.json._
import spray.json.DefaultJsonProtocol._

import cats.Monad

import monix.eval.Coeval

/** Contains constants and supporting case classes for [[ThermostatPostRequest]] */
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
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param selection The selection criteria for update
  * @param thermostat The thermostat and fields to modify.  Send only the minimum to be modified.
  * @param functions Any thermostat functions to execute after performing the modifications on `#thermostat`.
  * @tparam F Monad to contain the request
  */
case class ThermostatPostRequest[F[_]:Monad](
  override val account: AccountID, 
  override val tokenStore: Coeval[TokenStorage[F]], 
  selection : Select, 
  thermostat : Option[ThermostatModification], 
  functions : Option[Seq[ThermostatFunction]])(implicit s : Settings, log : LoggingBus)
extends Request[F,ThermostatPostRequest.RequestBody](account,tokenStore)
with PostRequest[F,ThermostatPostRequest.RequestBody] {

  import ThermostatPostRequest._

  val entity = Some(RequestBody(selection, thermostat, functions))
  val query = Coeval.pure(this.tokenIO.pure(List.empty[Querystrings.Entry]))
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
  * @tparam F The monad containing the request
  * @tparam M The container type that will hold results (from dependency injection, `DI`)
  */
class ThermostatPostService[F[_] : Monad,M[_]](implicit ev : LoggingBus) extends EcobeeJsonService[F,M,ThermostatPostRequest[F],ThermostatPostResponse] {

  /** Execute the request against the API, returning the [[ThermostatPostResponse]]
    *
    * @param account The ID of the account for which the token request will be made
    * @param tokenStore The store of all API tokens
    * @param selection The selection criteria for update
    * @param thermostat The thermostat and fields to modify.  Send only the minimum to be modified.
    * @param functions Any thermostat functions to execute after performing the modifications on `#thermostat`.
    */ 
  def execute(
    account: AccountID, 
    tokenStore: Coeval[TokenStorage[F]], 
    selectType : SelectType, 
    thermostat : Option[ThermostatModification] = None, 
    functions : Option[Seq[ThermostatFunction]] = None
  )(implicit e : RequestExecutor[F,M], s : Settings) : M[Either[ServiceError, ThermostatPostResponse]] = 
    execute(ThermostatPostRequest(account, tokenStore, Select(selectType), thermostat, functions))
}
