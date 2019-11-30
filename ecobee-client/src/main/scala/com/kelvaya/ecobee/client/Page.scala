package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Returned on paged content responses, or can be used to request specific pages */
case class Page(
    page :       Option[Int] = None,
    totalPages : Option[Int] = None,
    pageSize :   Option[Int] = None,
    total :      Option[Int] = None
) extends ReadonlyApiObject

object Page {
  implicit val PageFormat = DefaultJsonProtocol.jsonFormat4(Page.apply)
}