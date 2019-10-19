package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.storage.TokenStorage
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri

import cats.Monad

import spray.json._
import spray.json.DefaultJsonProtocol._

import monix.eval.Coeval

/** Constants used by [[ThermostatSummaryRequest]] */
object ThermostatSummaryRequest {
  private val Endpoint = Uri.Path("/thermostatSummary")

  private def getJson(st : SelectType, es : Boolean) = {
    Select(selectType = st, includeEquipmentStatus = es).toJson.compactPrint
  }
}


/** Request to return the summary information on selected [[Theromstat theromstats]].
  * 
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param selectType The thermostat to query
  * @param equipStatus True if the equipment status should also be returned
  */
case class ThermostatSummaryRequest[M[_]:Monad](override val account: AccountID, override val tokenStore: Coeval[TokenStorage[M]], selectType : SelectType, includeEquipStatus : Boolean = false)
(implicit s : Settings) extends RequestNoEntity[M](account, tokenStore) {
  import ThermostatSummaryRequest._

  val query: Coeval[M[List[Querystrings.Entry]]] = Coeval.pure(this.tokenIO.pure( (("selection", getJson(selectType, includeEquipStatus))) :: Nil ))
  val uri: Uri.Path = ThermostatSummaryRequest.Endpoint

}


// ############################################################
// ############################################################


/** Implicits for JSON serialization of [[TheromstatSummaryResponse]] */
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
  * @param tokenStore The store of all API tokens
  * @param selectType The thermostat to query
  * @param equipStatus True if the equipment status should also be returned
  * @param lb (implicit) The Akka logging bus
  * @param s (implicit) The application global settings
  *
  * @example
{{{
  val thermResponse = ThermostatSummaryService.execute(account, tokenStore, selectType, equipStatus)
}}}
  * @see [[ThermostatRequest]]
  */
object ThermostatSummaryService {
  
  /** Implicitly converts the object into a [[ThermostatSummary]] for the [[ThermostatSummaryRequest]] request
    *
    * This allows the syntax, `ThermostatSummaryService.execute`, to work instead of having to create both
    * an `ThermostatSummaryRequest` and pass it explicitly to a new `ThermostatSummaryServiceImpl`.
    */
  implicit class ThermostatSummaryServiceImpl[F[_]:Monad,M[_]](o : ThermostatSummaryService.type)(implicit lb : LoggingBus, s : Settings) extends EcobeeJsonService[F,M,ThermostatSummaryRequest[F],ThermostatSummaryResponse] {
    def execute(account: AccountID, tokenStore : Coeval[TokenStorage[F]], selectType : SelectType, includeEquipStatus : Boolean = false)(implicit e : RequestExecutor[F,M]): M[Either[ServiceError, ThermostatSummaryResponse]] =
      execute(ThermostatSummaryRequest(account, tokenStore, selectType, includeEquipStatus))
  }
}
