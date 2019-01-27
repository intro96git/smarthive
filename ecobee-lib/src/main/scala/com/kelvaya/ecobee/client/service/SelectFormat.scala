package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.client._

import akka.event.Logging
import akka.event.LoggingBus
import spray.json._


/** JSON formatter for the [[Select]] object **/
private[client] class SelectFormat(implicit lb : LoggingBus) extends RootJsonFormat[Select] {

  lazy val log = Logging(lb, this.getClass)

  def read(json : JsValue) : Select = json match {
    case j : JsObject ⇒ readJson(j)
    case _            ⇒ throw new DeserializationException(s"${json} is not a valid Select")

  }

  def write(obj : Select) : JsValue = {
    JsObject(
      Map(
        "selectionType" -> JsString(obj.selectType.id.toString),
        "selectionMatch" -> JsString(obj.selectType.selectionMatch),
        "includeRuntime" -> JsBoolean(obj.includeRuntime),
        "includeExtendedRuntime" -> JsBoolean(obj.includeExtendedRuntime),
        "includeElectricity" -> JsBoolean(obj.includeElectricity),
        "includeSettings" -> JsBoolean(obj.includeSettings),
        "includeLocation" -> JsBoolean(obj.includeLocation),
        "includeProgram" -> JsBoolean(obj.includeProgram),
        "includeEvents" -> JsBoolean(obj.includeEvents),
        "includeDevice" -> JsBoolean(obj.includeDevice),
        "includeTechnician" -> JsBoolean(obj.includeTechnician),
        "includeUtility" -> JsBoolean(obj.includeUtility),
        "includeManagement" -> JsBoolean(obj.includeManagement),
        "includeAlerts" -> JsBoolean(obj.includeAlerts),
        "includeReminders" -> JsBoolean(obj.includeReminders),
        "includeWeather" -> JsBoolean(obj.includeWeather),
        "includeHouseDetails" -> JsBoolean(obj.includeHouseDetails),
        "includeOemCfg" -> JsBoolean(obj.includeOemCfg),
        "includeEquipmentStatus" -> JsBoolean(obj.includeEquipmentStatus),
        "includeNotificationSettings" -> JsBoolean(obj.includeNotificationSettings),
        "includePrivacy" -> JsBoolean(obj.includePrivacy),
        "includeVersion" -> JsBoolean(obj.includeVersion),
        "includeSecuritySettings" -> JsBoolean(obj.includeSecuritySettings),
        "includeSensors" -> JsBoolean(obj.includeSensors),
        "includeAudio" -> JsBoolean(obj.includeAudio),
        "includeEnergy" -> JsBoolean(obj.includeEnergy)
      )
    )
  }


  // ################################################################################


  private def readJson(json : JsObject) : Select = {
    val ms = new MutableSelection()

    json.fields.map {
      case (("selectionType", s : JsString))                ⇒ ms.selectionType = Some(s.value)
      case (("selectionMatch", s : JsString))               ⇒ ms.selectionMatch = Some(s.value)
      case (("includeRuntime", b : JsBoolean))              ⇒ ms.includeRuntime = b.value
      case (("includeExtendedRuntime", b : JsBoolean))      ⇒ ms.includeExtendedRuntime = b.value
      case (("includeElectricity", b : JsBoolean))          ⇒ ms.includeElectricity = b.value
      case (("includeSettings", b : JsBoolean))             ⇒ ms.includeSettings = b.value
      case (("includeLocation", b : JsBoolean))             ⇒ ms.includeLocation = b.value
      case (("includeProgram", b : JsBoolean))              ⇒ ms.includeProgram = b.value
      case (("includeEvents", b : JsBoolean))               ⇒ ms.includeEvents = b.value
      case (("includeDevice", b : JsBoolean))               ⇒ ms.includeDevice = b.value
      case (("includeTechnician", b : JsBoolean))           ⇒ ms.includeTechnician = b.value
      case (("includeUtility", b : JsBoolean))              ⇒ ms.includeUtility = b.value
      case (("includeManagement", b : JsBoolean))           ⇒ ms.includeManagement = b.value
      case (("includeAlerts", b : JsBoolean))               ⇒ ms.includeAlerts = b.value
      case (("includeReminders", b : JsBoolean))            ⇒ ms.includeReminders = b.value
      case (("includeWeather", b : JsBoolean))              ⇒ ms.includeWeather = b.value
      case (("includeHouseDetails", b : JsBoolean))         ⇒ ms.includeHouseDetails = b.value
      case (("includeOemCfg", b : JsBoolean))               ⇒ ms.includeOemCfg = b.value
      case (("includeEquipmentStatus", b : JsBoolean))      ⇒ ms.includeEquipmentStatus = b.value
      case (("includeNotificationSettings", b : JsBoolean)) ⇒ ms.includeNotificationSettings = b.value
      case (("includePrivacy", b : JsBoolean))              ⇒ ms.includePrivacy = b.value
      case (("includeVersion", b : JsBoolean))              ⇒ ms.includeVersion = b.value
      case (("includeSecuritySettings", b : JsBoolean))     ⇒ ms.includeSecuritySettings = b.value
      case (("includeSensors", b : JsBoolean))              ⇒ ms.includeSensors = b.value
      case (("includeAudio", b : JsBoolean))                ⇒ ms.includeAudio = b.value
      case (("includeEnergy", b : JsBoolean))               ⇒ ms.includeEnergy = b.value
      case ((other, otherJson))                             ⇒ log.warning(s"Unexpected value found when parsing Select JSON object: '${other}' : ${otherJson}")
    }

    if (ms.selectionMatch.isEmpty || ms.selectionType.isEmpty)
      throw new DeserializationException(s"${json} is not a valid Select; missing selectionMatch and/or selectionType.")
    else
      Select(
        selectType = getSelectType(ms.selectionType.get,ms.selectionMatch.get),
        includeAlerts = ms.includeAlerts,
        includeAudio = ms.includeAudio,
        includeDevice = ms.includeDevice,
        includeElectricity = ms.includeElectricity,
        includeEnergy = ms.includeEnergy,
        includeEquipmentStatus = ms.includeEquipmentStatus,
        includeEvents = ms.includeEvents,
        includeExtendedRuntime = ms.includeExtendedRuntime,
        includeHouseDetails = ms.includeHouseDetails,
        includeLocation = ms.includeLocation,
        includeManagement = ms.includeManagement,
        includeNotificationSettings = ms.includeNotificationSettings,
        includeOemCfg = ms.includeOemCfg,
        includePrivacy = ms.includePrivacy,
        includeProgram = ms.includeProgram,
        includeReminders = ms.includeReminders,
        includeRuntime = ms.includeRuntime,
        includeSecuritySettings = ms.includeSecuritySettings,
        includeSensors = ms.includeSensors,
        includeSettings = ms.includeSettings,
        includeTechnician = ms.includeTechnician,
        includeUtility = ms.includeUtility,
        includeVersion = ms.includeVersion,
        includeWeather = ms.includeWeather
      )
  }



  private def getSelectType(selectionType : String, selectionMatch : String) = {
    try SelectType.create(selectionType, selectionMatch)
    catch {
      case _ : MatchError =>
        throw new DeserializationException(s"JSON for selection invalid; type '${selectionType}' and match '${selectionMatch}' is not supported.")
    }
  }


  private class MutableSelection(
    var selectionType :               Option[String] = None,
    var selectionMatch :              Option[String] = None,
    var includeRuntime :              Boolean = false,
    var includeExtendedRuntime :      Boolean = false,
    var includeElectricity :          Boolean = false,
    var includeSettings :             Boolean = false,
    var includeLocation :             Boolean = false,
    var includeProgram :              Boolean = false,
    var includeEvents :               Boolean = false,
    var includeDevice :               Boolean = false,
    var includeTechnician :           Boolean = false,
    var includeUtility :              Boolean = false,
    var includeManagement :           Boolean = false,
    var includeAlerts :               Boolean = false,
    var includeReminders :            Boolean = false,
    var includeWeather :              Boolean = false,
    var includeHouseDetails :         Boolean = false,
    var includeOemCfg :               Boolean = false,
    var includeEquipmentStatus :      Boolean = false,
    var includeNotificationSettings : Boolean = false,
    var includePrivacy :              Boolean = false,
    var includeVersion :              Boolean = false,
    var includeSecuritySettings :     Boolean = false,
    var includeSensors :              Boolean = false,
    var includeAudio :                Boolean = false,
    var includeEnergy :               Boolean = false
  )
}

