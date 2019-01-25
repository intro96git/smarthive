package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.Request
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.DefaultJsonProtocol._

object ThermostatSummaryRequest {
  implicit def ThermostatSummaryRequestFormat(implicit e : RequestExecutor, s : Settings) = DefaultJsonProtocol.jsonFormat1(ThermostatSummaryRequest.apply)

  private val Endpoint = Uri.Path("/thermostatSummary")
  private lazy val Format = implicitly[JsonFormat[Selection]]
}

case class ThermostatSummaryRequest(selection : Selection)(implicit e : RequestExecutor, s : Settings) extends Request {
  import ThermostatSummaryRequest._

  val entity: Option[String] = None
  val query: List[Querystrings.Entry] = (("selection", Format.write(selection).compactPrint)) :: Nil
  val uri: Uri.Path = ThermostatSummaryRequest.Endpoint

}


// ############################################################

object ThermostatSummaryResponse {
  implicit val ThermostatSummaryResponseFormat = DefaultJsonProtocol.jsonFormat4(ThermostatSummaryResponse.apply)
}
case class ThermostatSummaryResponse(revisionList : Seq[CSV], thermostatCount : Int, statusList : Seq[CSV], status : Status)


// ############################################################


object ThermostatSummaryService extends EcobeeJsonService[ThermostatSummaryRequest,ThermostatSummaryResponse] {
  def execute[R[_]](selection : Selection)(implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings): R[Either[ServiceError, ThermostatSummaryResponse]] =
    execute(new ThermostatSummaryRequest(selection))
}
