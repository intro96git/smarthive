package com.kelvaya.ecobee.client.service.function

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Removes the currently running event providing the event is not a mandatory demand response event
  *
  * @param resumeAll If false, resume to next event.  Otherwise, resume the thermostat's normal program (true).
  */
case class ResumeProgram(resumeAll : Boolean = false) extends EcobeeFunction[ResumeProgram] {
  val name = "resumeProgram"
  val params = this
  val writer = ResumeProgram.Format
}

object ResumeProgram {
  private lazy val Format = DefaultJsonProtocol.jsonFormat(ResumeProgram.apply _, "resumeAll")
}