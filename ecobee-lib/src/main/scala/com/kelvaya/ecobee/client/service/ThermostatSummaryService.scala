package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri

import cats.Monad

import spray.json._
import spray.json.DefaultJsonProtocol._

object ThermostatSummaryRequest {
  private val Endpoint = Uri.Path("/thermostatSummary")

  private def getJson(st : SelectType, es : Boolean) = {
    Select(selectType = st, includeEquipmentStatus = es).toJson.compactPrint
  }
}


case class ThermostatSummaryRequest[M[_]:Monad](selectType : SelectType, includeEquipStatus : Boolean = false)
(implicit e : RequestExecutor[M], s : Settings) extends RequestNoEntity[M] {
  import ThermostatSummaryRequest._

  val query: M[List[Querystrings.Entry]] = async.pure( (("selection", getJson(selectType, includeEquipStatus))) :: Nil )
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


class ThermostatSummaryService[M[_]:Monad](implicit lb : LoggingBus) extends EcobeeJsonService[M,ThermostatSummaryRequest[M],ThermostatSummaryResponse] {
  def execute[R[_]](selectType : SelectType, includeEquipStatus : Boolean = false)(implicit e : RequestExecutor[M], s : Settings): M[Either[ServiceError, ThermostatSummaryResponse]] =
    execute(new ThermostatSummaryRequest(selectType, includeEquipStatus))
}
