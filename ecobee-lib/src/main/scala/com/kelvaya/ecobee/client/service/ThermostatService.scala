package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client.AccountID
import com.kelvaya.ecobee.client.Page
import com.kelvaya.ecobee.client.Querystrings
import com.kelvaya.ecobee.client.RequestExecutor
import com.kelvaya.ecobee.client.RequestNoEntity
import com.kelvaya.ecobee.client.Status
import com.kelvaya.ecobee.client.Thermostat
import com.kelvaya.ecobee.config.Settings
import com.kelvaya.ecobee.client.storage.TokenStorage

import scala.language.higherKinds

import akka.event.LoggingBus
import akka.http.scaladsl.model.Uri

import spray.json._
import spray.json.DefaultJsonProtocol._

import cats.Monad

import monix.eval.Coeval

/** Factories for [[ThermostatRequest]] */
object ThermostatRequest {
  private val Endpoint = Uri.Path("/thermostat")

  /** Returns new [[TheromstatRequest]] to initially request information.
    *
    * @param account The ID of the account for which the token request will be made
    * @param tokenStore The store of all API tokens
    * @param selection The selection criteria for update
    * @param s (implicit) The application global settings
    */
 def apply[M[_] : Monad](account: AccountID, tokenStore: Coeval[TokenStorage[M]], selection : Select)(implicit s : Settings) : ThermostatRequest[M] = ThermostatRequest(account, tokenStore, selection, None)
  
  /** Returns new [[TheromstatRequest]] to request information from a starting page.
    *
    * @param account The ID of the account for which the token request will be made
    * @param tokenStore The store of all API tokens
    * @param selection The selection criteria for update
    * @param page The page of information to return
    * @param s (implicit) The application global settings
    */
 def apply[M[_] : Monad](account: AccountID, tokenStore: Coeval[TokenStorage[M]], selection : Select, page : Int)(implicit s : Settings) : ThermostatRequest[M] = ThermostatRequest(account, tokenStore, selection, Some(page))
}


/** Request for [[Thermostat]] information
  * 
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param selection The selection criteria for update
  * @param page The page of information to return
  * @param s (implicit) The application global settings
  */
case class ThermostatRequest[M[_] : Monad](override val account: AccountID, override val tokenStore: Coeval[TokenStorage[M]], selection : Select, page : Option[Int] = None)
(implicit s : Settings) extends RequestNoEntity[M](account, tokenStore) {

  val pageQS : Option[Querystrings.Entry] = page map { p => (("page", Page(Some(p), None, None, None).toJson.compactPrint )) }

  val query: Coeval[M[List[Querystrings.Entry]]] =  Coeval.pure( this.tokenIO.pure {
    val list = collection.mutable.ListBuffer((("selection", selection.toJson.compactPrint)))
    if (pageQS.isDefined) list += pageQS.get
    list.toList
  })
  val uri: Uri.Path = ThermostatRequest.Endpoint

}


// ############################################################
// ############################################################

/** Implicits for JSON serialization of [[TheromstatResponse]] */
object ThermostatResponse {
  implicit def responseFormat(implicit ev : LoggingBus) = DefaultJsonProtocol.jsonFormat3(ThermostatResponse.apply)
}


/** Response from a [[TheromstatRequest]]
  * 
  * @param theromostatList The list of theromstats
  * @param page The page of information on the thermostats
  * @param status The return status from the API
  */
case class ThermostatResponse(
    thermostatList : Seq[Thermostat],
    page :           Page,
    status :         Status
)


// ############################################################
// ############################################################



/** Service for requesting information on a [[Thermostat]]
  *
  * @param account The ID of the account for which the token request will be made
  * @param tokenStore The store of all API tokens
  * @param selection The selection criteria for update
  * @param page The page of information to return
  * @param s (implicit) The application global settings
  *
  * @example
{{{
  val thermResponse = ThermostatService.execute(account, tokenStore, selection)
}}}
  * @see [[ThermostatRequest]]
  */
object ThermostatService {


  /** Implicitly converts the object into a [[ThermostatService]] for the [[ThermostatRequest]] request
    *
    * This allows the syntax, `ThermostatService.execute`, to work instead of having to create both
    * an `ThermostatRequest` and pass it explicitly to a new `ThermostatServiceImpl`.
    */
  implicit class ThermostatServiceImpl[F[_] : Monad,M[_]](o : ThermostatService.type)(implicit ev : LoggingBus, s : Settings) extends EcobeeJsonService[F, M, ThermostatRequest[F],ThermostatResponse] {

    def execute(account : AccountID, tokenStore: Coeval[TokenStorage[F]], selection : Select)(implicit e : RequestExecutor[F,M]): M[Either[ServiceError, ThermostatResponse]] =
      pexecute(account, tokenStore, selection, None)

    def execute(account : AccountID, tokenStore: Coeval[TokenStorage[F]], selection : Select, page : Int)(implicit e : RequestExecutor[F,M]): M[Either[ServiceError, ThermostatResponse]] =
      pexecute(account, tokenStore, selection, Some(page))

    private def pexecute(account : AccountID, store: Coeval[TokenStorage[F]], selection : Select, page : Option[Int])(implicit e : RequestExecutor[F,M]): M[Either[ServiceError, ThermostatResponse]] =
      execute(ThermostatRequest(account, store, selection, page))
  }
}