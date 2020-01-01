package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.AuthorizedRequest
import com.kelvaya.ecobee.client.ParameterlessApi
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.ServiceError
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.ClientSettings

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri

import spray.json._
import spray.json.DefaultJsonProtocol._

import zio.UIO
import zio.ZIO

/** Constants used by [[ThermostatSummaryRequest]] */
object ThermostatSummaryRequest {
  private val Endpoint = Uri.Path("/thermostatSummary")

  private def getJson(st : SelectType, es : Boolean) = {
    Select(selectType = st, includeEquipmentStatus = es).toJson.compactPrint
  }
}


/** Request to return the summary information on selected [[Thermostat theromstats]].
  * 
  * @param account The ID of the account for which the token request will be made
  * @param selectType The thermostat to query
  * @param equipStatus True if the equipment status should also be returned
  */
case class ThermostatSummaryRequest(override val account: AccountID, selectType : SelectType, includeEquipStatus : Boolean = false)
(implicit s : ClientSettings) extends RequestNoEntity(account) with AuthorizedRequest[ParameterlessApi] {
  import ThermostatSummaryRequest._

  val query: UIO[List[Querystrings.Entry]] = UIO( (("selection", getJson(selectType, includeEquipStatus))) :: Nil )
  val uri: Uri.Path = ThermostatSummaryRequest.Endpoint

}


// ############################################################
// ############################################################


/** Implicits for JSON serialization of [[ThermostatSummaryResponse]] */
object ThermostatSummaryResponse {
  implicit def getResponseFormat(implicit lb : LoggingBus) = DefaultJsonProtocol.jsonFormat4(ThermostatSummaryResponse.apply)
}

/** Response from a [[ThermostatSummaryRequest]]
  * 
  * @param revisionList The list of revisions of the theromstat data
  * @param thermostatCount The number of thermostats returned in the data
  * @param statusList The list of equipment status, if the request asked for them
  * @param status The overall return status of the request
  */
case class ThermostatSummaryResponse(
    revisionList :    Seq[RevisionListItem],
    thermostatCount : Int,
    statusList :      Option[Seq[EquipmentStatusListItem]],
    status :          Status
)


// ############################################################
// ############################################################

/** Service for requesting summary information on a [[Thermostat]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param selectType The thermostat to query
  * @param equipStatus True if the equipment status should also be returned
  * @param lb (implicit) The Akka logging bus
  * @param s (implicit) The application global settings
  *
  * @example
{{{
  val thermResponse = ThermostatSummaryService.execute(account, selectType, equipStatus)
}}}
  * @see [[ThermostatRequest]]
  */
object ThermostatSummaryService {
  
  /** Implicitly converts the object into a [[ThermostatSummaryService]] for the [[ThermostatSummaryRequest]] request
    *
    * This allows the syntax, `ThermostatSummaryService.execute`, to work instead of having to create both
    * an `ThermostatSummaryRequest` and pass it explicitly to a new `ThermostatSummaryServiceImpl`.
    */
  implicit class ThermostatSummaryServiceImpl(o : ThermostatSummaryService.type)(implicit lb : LoggingBus, s : ClientSettings) extends EcobeeJsonService[ThermostatSummaryRequest,ThermostatSummaryResponse] {
    def execute(account: AccountID, selectType : SelectType, includeEquipStatus : Boolean = false): ZIO[ClientEnv,ServiceError,ThermostatSummaryResponse] =
      execute(ThermostatSummaryRequest(account, selectType, includeEquipStatus))
  }
}
