package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.Page
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.Thermostat
import com.kelvaya.ecobee.config.Settings

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri
import spray.json._
import spray.json.DefaultJsonProtocol._

import cats.Monad

object ThermostatRequest {
  private val Endpoint = Uri.Path("/thermostat")

  def apply[M[_] : Monad](selection : Select)(implicit e : RequestExecutor[M], s : Settings) : ThermostatRequest[M] = ThermostatRequest(selection, None)
  def apply[M[_] : Monad](selection : Select, page : Int)(implicit e : RequestExecutor[M], s : Settings) : ThermostatRequest[M] = ThermostatRequest(selection, Some(page))
}


case class ThermostatRequest[M[_] : Monad](selection : Select, page : Option[Int] = None)
(implicit e : RequestExecutor[M], s : Settings) extends RequestNoEntity[M] {

  val pageQs : Option[Querystrings.Entry] = page map { p => (("page", Page(Some(p), None, None, None).toJson.compactPrint )) }

  val query: M[List[Querystrings.Entry]] =  async.pure {
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

class ThermostatService[M[_] : Monad] private (_select : Select, _page : Option[Int])(implicit  ev : LoggingBus) extends EcobeeJsonService[M, ThermostatRequest[M],ThermostatResponse] {

  def this(selection : Select)(implicit  ev : LoggingBus) = this(selection, None)
  def this(selection : Select, page : Int)(implicit  ev : LoggingBus) = this(selection, Some(page))

  def execute()(implicit e : RequestExecutor[M], s : Settings): M[Either[ServiceError, ThermostatResponse]] =
    execute(ThermostatRequest(_select, _page))
}
