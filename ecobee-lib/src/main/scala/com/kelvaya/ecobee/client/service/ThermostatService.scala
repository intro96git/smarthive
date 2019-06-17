package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.client.Page
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.Thermostat
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.util.Realizer

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.DefaultJsonProtocol._

object ThermostatRequest {
  private val Endpoint = Uri.Path("/thermostat")

  def apply(selection : Select)(implicit e : RequestExecutor, s : Settings) : ThermostatRequest = ThermostatRequest(selection, None)
  def apply(selection : Select, page : Int)(implicit e : RequestExecutor, s : Settings) : ThermostatRequest = ThermostatRequest(selection, Some(page))
}


case class ThermostatRequest(selection : Select, page : Option[Int] = None)
(implicit e : RequestExecutor, s : Settings) extends RequestNoEntity {
  import ThermostatRequest._

  val pageQs : Option[Querystrings.Entry] = page map { p => (("page", Page(Some(p), None, None, None).toJson.compactPrint )) }

  val query: List[Querystrings.Entry] =  {
    val list = collection.mutable.ListBuffer((("selection", selection.toJson.compactPrint)))
    if (pageQs.isDefined) list += pageQs.get
    list.toList
  }
  val uri: Uri.Path = ThermostatRequest.Endpoint

}


// ############################################################
// ############################################################

object ThermostatResponse {
  implicit def responseFormat(implicit ev : LoggingBus) = DefaultJsonProtocol.jsonFormat3(ThermostatResponse.apply)
}

case class ThermostatResponse(
    thermostatList : Seq[Thermostat],
    page :           Page,
    status :         Status
)


// ############################################################
// ############################################################

class ThermostatService private (_select : Select, _page : Option[Int])(implicit  ev : LoggingBus) extends EcobeeJsonService[ThermostatRequest,ThermostatResponse] {

  def this(selection : Select)(implicit  ev : LoggingBus) = this(selection, None)
  def this(selection : Select, page : Int)(implicit  ev : LoggingBus) = this(selection, Some(page))

  def execute[R[_]]()(implicit r: Realizer[R], c: Client, e : RequestExecutor, s : Settings): R[Either[ServiceError, ThermostatResponse]] =
    execute(ThermostatRequest(_select, _page))
}