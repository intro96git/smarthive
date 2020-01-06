package com.kelvaya.ecobee.client.service

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits

/** JSON formatter for the [[Select]] object **/
private[client] class SelectFormat extends RootJsonFormat[Select] with SprayImplicits {

  def read(json : JsValue) : Select = json match {
    case j : JsObject ⇒ readJson(j)
    case _            ⇒ throw new DeserializationException(s"${json} is not a valid Select")

  }

  def write(obj : Select) : JsValue = {
    val fullMap = Map(
        "selectionType" -> JsString(obj.selectType.id.toString),
        "selectionMatch" -> obj.selectType.selectionMatch.map(m =>JsString(m)).getOrElse(JsBoolean(false)),
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

    // NB: Remove any "false" values from map to make requests more concise
    val filteredMap = fullMap.filter {
      case ((_,v : JsBoolean)) => v.value
      case _ => true
    }

    JsObject(filteredMap)
  }


  // ################################################################################


  private def readJson(json : JsObject) : Select = {

    val ms = new MutableSelection()

    findOptional[String](json, "selectionType") foreach { v => ms.selectionType = Some(v) }
    findOptional[String](json, "selectionMatch") foreach { v => ms.selectionMatch = Some(v) }
    findOptional[Boolean](json, "includeRuntime") foreach { ms.includeRuntime = _ }
    findOptional[Boolean](json, "includeExtendedRuntime") foreach { ms.includeExtendedRuntime = _ }
    findOptional[Boolean](json, "includeElectricity") foreach { ms.includeElectricity = _ }
    findOptional[Boolean](json, "includeSettings") foreach { ms.includeSettings = _ }
    findOptional[Boolean](json, "includeLocation") foreach { ms.includeLocation = _ }
    findOptional[Boolean](json, "includeProgram") foreach { ms.includeProgram = _ }
    findOptional[Boolean](json, "includeEvents") foreach { ms.includeEvents = _ }
    findOptional[Boolean](json, "includeDevice") foreach { ms.includeDevice = _ }
    findOptional[Boolean](json, "includeTechnician") foreach { ms.includeTechnician = _ }
    findOptional[Boolean](json, "includeUtility") foreach { ms.includeUtility = _ }
    findOptional[Boolean](json, "includeManagement") foreach { ms.includeManagement = _ }
    findOptional[Boolean](json, "includeAlerts") foreach { ms.includeAlerts = _ }
    findOptional[Boolean](json, "includeReminders") foreach { ms.includeReminders = _ }
    findOptional[Boolean](json, "includeWeather") foreach { ms.includeWeather = _ }
    findOptional[Boolean](json, "includeHouseDetails") foreach { ms.includeHouseDetails = _ }
    findOptional[Boolean](json, "includeOemCfg") foreach { ms.includeOemCfg = _ }
    findOptional[Boolean](json, "includeEquipmentStatus") foreach { ms.includeEquipmentStatus = _ }
    findOptional[Boolean](json, "includeNotificationSettings") foreach { ms.includeNotificationSettings = _ }
    findOptional[Boolean](json, "includePrivacy") foreach { ms.includePrivacy = _ }
    findOptional[Boolean](json, "includeVersion") foreach { ms.includeVersion = _ }
    findOptional[Boolean](json, "includeSecuritySettings") foreach { ms.includeSecuritySettings = _ }
    findOptional[Boolean](json, "includeSensors") foreach { ms.includeSensors = _ }
    findOptional[Boolean](json, "includeAudio") foreach { ms.includeAudio = _ }
    findOptional[Boolean](json, "includeEnergy") foreach { ms.includeEnergy = _ }

    if (ms.selectionType.isEmpty)
      throw new DeserializationException(s"${json} is not a valid Select; missing selectionType.")
    else
      Select(
        selectType = getSelectType(ms.selectionType.get,ms.selectionMatch),
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



  private def getSelectType(selectionType : String, selectionMatch : Option[String]) = {
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
