package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.DefaultJsonProtocol._

object ThermostatSummaryRequest {
  private val Endpoint = Uri.Path("/thermostatSummary")

  private def getJson(st : SelectType, es : Boolean)(implicit lb : LoggingBus) = {
    Select(selectType = st, includeEquipmentStatus = es).toJson.compactPrint
  }
}


case class ThermostatSummaryRequest(selectType : SelectType, includeEquipStatus : Boolean = false)
(implicit e : RequestExecutor, s : Settings, lb : LoggingBus) extends Request {
  import ThermostatSummaryRequest._

  val entity: Option[String] = None
  val query: List[Querystrings.Entry] = (("selection", getJson(selectType, includeEquipStatus))) :: Nil
  val uri: Uri.Path = ThermostatSummaryRequest.Endpoint

}


// ############################################################
// ############################################################

object ThermostatSummaryResponse {
  implicit def getResponseFormat(implicit lb : LoggingBus) = DefaultJsonProtocol.jsonFormat4(ThermostatSummaryResponse.apply)
}

case class ThermostatSummaryResponse(
    revisionList :    Seq[RevisionListItem],
    thermostatCount : Int,
    statusList :      Option[Seq[EquipmentStatusListItem]],
    status :          Status
)


// ############################################################
// ############################################################


class ThermostatSummaryService(implicit lb : LoggingBus) extends EcobeeJsonService[ThermostatSummaryRequest,ThermostatSummaryResponse] {
  def execute[R[_]](selectType : SelectType, includeEquipStatus : Boolean = false)(implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings): R[Either[ServiceError, ThermostatSummaryResponse]] =
    execute(new ThermostatSummaryRequest(selectType, includeEquipStatus))
}
