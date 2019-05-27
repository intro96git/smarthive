package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time.FullDate

/** All settings that a Thermostat may use that can be modified in a POST request.
  *
  * @param lastServiceDate The last service date of the HVAC equipment.
  * @param serviceRemindMe Whether to send an alert when service is required again.
  * @param monthsBetweenService The user configured monthly interval between HVAC service reminders
  * @param remindMeDate Date to be reminded about the next HVAC service date.
  * @param vent The ventilator mode.
  * @param ventilatorMinOnTime The minimum time in minutes the ventilator is configured to run. The thermostat will always guarantee that the ventilator runs for this minimum duration whenever engaged.
  * @param serviceRemindTechnician Whether the technician associated with this thermostat should receive the HVAC service reminders as well.
  * @param eiLocation A note about the physical location where the SMART or EMS Equipment Interface module is located.
  * @param coldTempAlert The temperature at which a cold temp alert is triggered.
  * @param coldTempAlertEnabled Whether cold temperature alerts are enabled.
  * @param hotTempAlert The temperature at which a hot temp alert is triggered.
  * @param hotTempAlertEnabled Whether hot temperature alerts are enabled.
  * @param maxSetBack The maximum automated set point set back offset allowed in degrees.
  * @param maxSetForward The maximum automated set point set forward offset allowed in degrees.
  * @param quickSaveSetBack The set point set back offset, in degrees, configured for a quick save event.
  * @param quickSaveSetForward The set point set forward offset, in degrees, configured for a quick save event.
  * @param condensationAvoid Whether the thermostat is in frost control mode.
  * @param useCelsius Whether the thermostat is configured to report in degrees Celcius.
  * @param useTimeFormat12 Whether the thermostat is using 12hr time format.
  * @param locale Multilanguage support.
  * @param humidity The minimum humidity level (in percent) set point for the humidifier
  * @param humidifierMode The humidifier mode
  * @param backlightOnIntensity The thermostat backlight intensity when on. A value between 0 and 10, with 0 meaning 'off'
  * @param backlightSleepIntensity The thermostat backlight intensity when asleep. A value between 0 and 10, with 0 meaning 'off'
  * @param backlightOffTime The time in seconds before the thermostat screen goes into sleep mode.
  * @param compressorProtectionMinTime The minimum time the compressor must be off for in order to prevent short-cycling.
  * @param compressorProtectionMinTemp The minimum outdoor temperature that the compressor can operate at.
  * @param stage1HeatingDifferentialTemp The difference between current temperature and set-point that will trigger stage 2 heating.
  * @param stage1CoolingDifferentialTemp The difference between current temperature and set-point that will trigger stage 2 cooling.
  * @param stage1HeatingDissipationTime The time after a heating cycle that the fan will run for to extract any heating left in the system.
  * @param stage1CoolingDissipationTime The time after a cooling cycle that the fan will run for to extract any cooling left in the system
  * @param heatPumpReversalOnCool The flag to tell if the heat pump is in heating mode or in cooling when the relay is engaged. If false, it's heating when the reversing valve is open.
  * @param fanControlRequired Whether fan control by the Thermostat is required in auxiliary heating (gas/electric/boiler), otherwise controlled by furnace.
  * @param fanMinOnTime The minimum time, in minutes, to run the fan each hour. Value from 1 to 60.
  * @param heatCoolMinDelta The minimum temperature difference between the heat and cool values.
  * @param tempCorrection The amount to adjust the temperature reading in degrees F.
  * @param holdAction The default end time setting the thermostat applies to user temperature holds.
  * @param dehumidifierMode The dehumidifier mode.If set to off then the dehumidifier will not run, nor will the AC overcool run.
  * @param dehumidifierLevel The dehumidification set point in percentage.
  * @param dehumidifyWithAC Whether the thermostat should use AC overcool to dehumidify. When set to true a postive integer value must be supplied for dehumidifyOvercoolOffset.
  * @param dehumidifyOvercoolOffset Whether the thermostat should use AC overcool to dehumidify and what that temperature offset should be. A value of 0 means this feature is disabled. Value represents the value in F to subract from the current set point. Values should be in the range 0 - 50 and be divisible by 5.
  * @param autoHeatCoolFeatureEnabled If enabled, allows the Thermostat to be put in HVACAuto mode.
  * @param wifiOfflineAlert Whether the alert for when wifi is offline is enabled.
  * @param heatRangeHigh The maximum heat set point configured by the user's preferences.
  * @param heatRangeLow The minimum heat set point configured by the user's preferences.
  * @param coolRangeHigh The maximum cool set point configured by the user's preferences.
  * @param coolRangeLow The minimum heat set point configured by the user's preferences.
  * @param auxRuntimeAlert The temperature at which an auxHeat temperature alert is triggered.
  * @param auxOutdoorTempAlert The temperature at which an auxOutdoor temperature alert is triggered.
  * @param auxMaxOutdoorTemp The maximum outdoor temperature above which aux heat will not run.
  * @param auxRuntimeAlertNotify Whether the auxHeat temperature alerts are enabled.
  * @param auxOutdoorTempAlertNotify Whether the auxOutdoor temperature alerts are enabled.
  * @param auxRuntimeAlertNotifyTechnician Whether the auxHeat temperature alerts for the technician are enabled.
  * @param auxOutdoorTempAlertNotifyTechnician Whether the auxOutdoor temperature alerts for the technician are enabled.
  * @param disablePreHeating Whether the thermostat should use pre heating to reach the set point on time.
  * @param disablePreCooling Whether the thermostat should use pre cooling to reach the set point on time.
  * @param installerCodeRequired Whether an installer code is required.
  * @param drAccept Whether Demand Response requests are accepted by this thermostat.
  * @param isRentalProperty Whether the property is a rental, or not.
  * @param useZoneController Whether to use a zone controller or not.
  * @param randomStartDelayCool Whether random start delay is enabled for cooling.
  * @param randomStartDelayHeat Whether random start delay is enabled for heating.
  * @param humidityHighAlert The humidity level to trigger a high humidity alert.
  * @param humidityLowAlert The humidity level to trigger a low humidity alert.
  * @param disableHeatPumpAlerts Whether heat pump alerts are disabled.
  * @param disableAlertsOnIdt Whether alerts are disabled from showing on the thermostat.
  * @param humidityAlertNotify Whether humidification alerts are enabled to the thermsotat ownDateTimeer.
  * @param humidityAlertNotifyTechnician Whether humidification alerts are enabled to the technician associated with the thermsotat.
  * @param tempAlertNotify Whether temperature alerts are enabled to the thermsotat owner.
  * @param tempAlertNotifyTechnician Whether temperature alerts are enabled to the technician associated with the thermostat.
  * @param monthlyElectricityBillLimit The dollar amount the owner specifies for their desired maximum electricy bill.
  * @param enableElectricityBillAlert Whether electricity bill alerts are enabled.
  * @param enableProjectedElectricityBillAlert Whether electricity bill projection alerts are enabled
  * @param electricityBillingDayOfMonth The day of the month the owner's electricty usage is billed.
  * @param electricityBillCycleMonths The owners billing cycle duration in months.
  * @param electricityBillStartMonth The annual start month of the owners billing cycle.
  * @param ventilatorMinOnTimeHome The number of minutes to run ventilator per hour when home.
  * @param ventilatorMinOnTimeAway The number of minutes to run ventilator per hour when away.
  * @param backlightOffDuringSleep Determines whether or not to turn the backlight off during sleep.
  * @param autoAway When set to true if no occupancy motion detected thermostat will go into indefinite away hold, until either the user presses resume schedule or motion is detected.
  * @param smartCirculation When set to true if a larger than normal delta is found between sensors the fan will be engaged for 15min/hour.
  * @param followMeComfort When set to true if a sensor has detected presense for more than 10 minutes then include that sensor in temp average. If no activity has been seen on a sensor for more than 1 hour then remove this sensor from temperature average.
  * @param isVentilatorTimerOn This Boolean field represents whether the ventilator timer is on or off. The default value is false. If set to true the ventilatorOffDateTime is set to now() + 20 minutes. If set to false the ventilatorOffDateTime is set to it's default value.
  * @param hasUVFilter This Boolean field represents whether the HVAC system has a UV filter.
  * @param coolingLockout This field represents whether to permit the cooling to operate when the Outdoor temeperature is under a specific threshold, currently 55F. The default value is false.
  * @param ventilatorFreeCooling Whether to use the ventilator to dehumidify when climate or calendar event indicates the owner is home.
  * @param dehumidifyWhenHeating This field represents whether to permit dehumidifer to operate when the heating is running.
  * @param ventilatorDehumidify This field represents whether or not to allow dehumification when cooling.
  * @param groupRef The unique reference to the group this thermostat belongs to.
  * @param groupName The name of the the group this thermostat belongs to.
  * @param groupSetting The setting value for the group this thermostat belongs to.
  *
  * @see ThermostatSettings
  */
case class ThermostatSettingsModification(
    hvacMode :                            Option[HVACMode.Entry] = None,
    lastServiceDate :                     Option[FullDate] = None,
    serviceRemindMe :                     Option[Boolean] = None,
    monthsBetweenService :                Option[Int] = None,
    remindMeDate :                        Option[FullDate] = None,
    vent :                                Option[VentilatorMode.Entry] = None,
    ventilatorMinOnTime :                 Option[Int] = None,
    serviceRemindTechnician :             Option[Boolean] = None,
    eiLocation :                          Option[String] = None,
    coldTempAlert :                       Option[Int] = None,
    coldTempAlertEnabled :                Option[Boolean] = None,
    hotTempAlert :                        Option[Int] = None,
    hotTempAlertEnabled :                 Option[Boolean] = None,
    maxSetBack :                          Option[Int] = None,
    maxSetForward :                       Option[Int] = None,
    quickSaveSetBack :                    Option[Int] = None,
    quickSaveSetForward :                 Option[Int] = None,
    condensationAvoid :                   Option[Boolean] = None,
    useCelsius :                          Option[Boolean] = None,
    useTimeFormat12 :                     Option[Boolean] = None,
    locale :                              Option[String] = None,
    humidity :                            Option[String] = None,
    humidifierMode :                      Option[HumidifierMode.Entry] = None,
    backlightOnIntensity :                Option[Int] = None,
    backlightSleepIntensity :             Option[Int] = None,
    backlightOffTime :                    Option[Int] = None,
    compressorProtectionMinTime :         Option[Int] = None,
    compressorProtectionMinTemp :         Option[Int] = None,
    stage1HeatingDifferentialTemp :       Option[Int] = None,
    stage1CoolingDifferentialTemp :       Option[Int] = None,
    stage1HeatingDissipationTime :        Option[Int] = None,
    stage1CoolingDissipationTime :        Option[Int] = None,
    heatPumpReversalOnCool :              Option[Boolean] = None,
    fanControlRequired :                  Option[Boolean] = None,
    fanMinOnTime :                        Option[Int] = None,
    heatCoolMinDelta :                    Option[Int] = None,
    tempCorrection :                      Option[Int] = None,
    holdAction :                          Option[HoldAction.Entry] = None,
    dehumidifierMode :                    Option[DehumidifierMode.Entry] = None,
    dehumidifierLevel :                   Option[Int] = None,
    dehumidifyWithAC :                    Option[Boolean] = None,
    dehumidifyOvercoolOffset :            Option[Int] = None,
    autoHeatCoolFeatureEnabled :          Option[Boolean] = None,
    wifiOfflineAlert :                    Option[Boolean] = None,
    heatRangeHigh :                       Option[Int] = None,
    heatRangeLow :                        Option[Int] = None,
    coolRangeHigh :                       Option[Int] = None,
    coolRangeLow :                        Option[Int] = None,
    auxRuntimeAlert :                     Option[Int] = None,
    auxOutdoorTempAlert :                 Option[Int] = None,
    auxMaxOutdoorTemp :                   Option[Int] = None,
    auxRuntimeAlertNotify :               Option[Boolean] = None,
    auxOutdoorTempAlertNotify :           Option[Boolean] = None,
    auxRuntimeAlertNotifyTechnician :     Option[Boolean] = None,
    auxOutdoorTempAlertNotifyTechnician : Option[Boolean] = None,
    disablePreHeating :                   Option[Boolean] = None,
    disablePreCooling :                   Option[Boolean] = None,
    installerCodeRequired :               Option[Boolean] = None,
    drAccept :                            Option[DRAccept.Entry] = None,
    isRentalProperty :                    Option[Boolean] = None,
    useZoneController :                   Option[Boolean] = None,
    randomStartDelayCool :                Option[Int] = None,
    randomStartDelayHeat :                Option[Int] = None,
    humidityHighAlert :                   Option[Int] = None,
    humidityLowAlert :                    Option[Int] = None,
    disableHeatPumpAlerts :               Option[Boolean] = None,
    disableAlertsOnIdt :                  Option[Boolean] = None,
    humidityAlertNotify :                 Option[Boolean] = None,
    humidityAlertNotifyTechnician :       Option[Boolean] = None,
    tempAlertNotify :                     Option[Boolean] = None,
    tempAlertNotifyTechnician :           Option[Boolean] = None,
    monthlyElectricityBillLimit :         Option[Int] = None,
    enableElectricityBillAlert :          Option[Boolean] = None,
    enableProjectedElectricityBillAlert : Option[Boolean] = None,
    electricityBillingDayOfMonth :        Option[Int] = None,
    electricityBillCycleMonths :          Option[Int] = None,
    electricityBillStartMonth :           Option[Int] = None,
    ventilatorMinOnTimeHome :             Option[Int] = None,
    ventilatorMinOnTimeAway :             Option[Int] = None,
    backlightOffDuringSleep :             Option[Boolean] = None,
    autoAway :                            Option[Boolean] = None,
    smartCirculation :                    Option[Boolean] = None,
    followMeComfort :                     Option[Boolean] = None,
    isVentilatorTimerOn :                 Option[Boolean] = None,
    hasUVFilter :                         Option[Boolean] = None,
    coolingLockout :                      Option[Boolean] = None,
    ventilatorFreeCooling :               Option[Boolean] = None,
    dehumidifyWhenHeating :               Option[Boolean] = None,
    ventilatorDehumidify :                Option[Boolean] = None,
    groupRef :                            Option[String] = None,
    groupName :                           Option[String] = None,
    groupSetting :                        Option[Int] = None
) extends WriteableApiObject


object ThermostatSettingsModification {

  import SprayImplicits._

  implicit object ThermostatSettingsFormat extends RootJsonFormat[ThermostatSettingsModification] {
    def read(json : JsValue) : ThermostatSettingsModification = json match {
      case obj : JsObject ⇒ {
        ThermostatSettingsModification(
          hvacMode                            = findOptional[HVACMode.Entry](obj, "hvacMode"),
          lastServiceDate                     = findOptional[FullDate](obj, "lastServiceDate"),
          serviceRemindMe                     = findOptional[Boolean](obj, "serviceRemindMe"),
          monthsBetweenService                = findOptional[Int](obj, "obj.monthsBetweenService"),
          remindMeDate                        = findOptional[FullDate](obj, "remindMeDate"),
          vent                                = findOptional[VentilatorMode.Entry](obj, "vent"),
          ventilatorMinOnTime                 = findOptional[Int](obj, "ventilatorMinOnTime"),
          serviceRemindTechnician             = findOptional[Boolean](obj, "serviceRemindTechnician"),
          eiLocation                          = findOptional[String](obj, "eiLocation"),
          coldTempAlert                       = findOptional[Int](obj, "coldTempAlert"),
          coldTempAlertEnabled                = findOptional[Boolean](obj, "coldTempAlertEnabled"),
          hotTempAlert                        = findOptional[Int](obj, "hotTempAlert"),
          hotTempAlertEnabled                 = findOptional[Boolean](obj, "hotTempAlertEnabled"),
          maxSetBack                          = findOptional[Int](obj, "maxSetBack"),
          maxSetForward                       = findOptional[Int](obj, "maxSetForward"),
          quickSaveSetBack                    = findOptional[Int](obj, "quickSaveSetBack"),
          quickSaveSetForward                 = findOptional[Int](obj, "quickSaveSetForward"),
          condensationAvoid                   = findOptional[Boolean](obj, "condensationAvoid"),
          useCelsius                          = findOptional[Boolean](obj, "useCelsius"),
          useTimeFormat12                     = findOptional[Boolean](obj, "useTimeFormat12"),
          locale                              = findOptional[String](obj, "locale"),
          humidity                            = findOptional[String](obj, "humidity"),
          humidifierMode                      = findOptional[HumidifierMode.Entry](obj, "humidifierMode"),
          backlightOnIntensity                = findOptional[Int](obj, "backlightOnIntensity"),
          backlightSleepIntensity             = findOptional[Int](obj, "backlightSleepIntensity"),
          backlightOffTime                    = findOptional[Int](obj, "backlightOffTime"),
          compressorProtectionMinTime         = findOptional[Int](obj, "compressorProtectionMinTime"),
          compressorProtectionMinTemp         = findOptional[Int](obj, "compressorProtectionMinTemp"),
          stage1HeatingDifferentialTemp       = findOptional[Int](obj, "stage1HeatingDifferentialTemp"),
          stage1CoolingDifferentialTemp       = findOptional[Int](obj, "stage1CoolingDifferentialTemp"),
          stage1HeatingDissipationTime        = findOptional[Int](obj, "stage1HeatingDissipationTime"),
          stage1CoolingDissipationTime        = findOptional[Int](obj, "stage1CoolingDissipationTime"),
          heatPumpReversalOnCool              = findOptional[Boolean](obj, "heatPumpReversalOnCool"),
          fanControlRequired                  = findOptional[Boolean](obj, "fanControlRequired"),
          fanMinOnTime                        = findOptional[Int](obj, "fanMinOnTime"),
          heatCoolMinDelta                    = findOptional[Int](obj, "heatCoolMinDelta"),
          tempCorrection                      = findOptional[Int](obj, "tempCorrection"),
          holdAction                          = findOptional[HoldAction.Entry](obj, "holdAction"),
          dehumidifierMode                    = findOptional[DehumidifierMode.Entry](obj, "dehumidifierMode"),
          dehumidifierLevel                   = findOptional[Int](obj, "dehumidifierLevel"),
          dehumidifyWithAC                    = findOptional[Boolean](obj, "dehumidifyWithAC"),
          dehumidifyOvercoolOffset            = findOptional[Int](obj, "dehumidifyOvercoolOffset"),
          autoHeatCoolFeatureEnabled          = findOptional[Boolean](obj, "autoHeatCoolFeatureEnabled"),
          wifiOfflineAlert                    = findOptional[Boolean](obj, "wifiOfflineAlert"),
          heatRangeHigh                       = findOptional[Int](obj, "heatRangeHigh"),
          heatRangeLow                        = findOptional[Int](obj, "heatRangeLow"),
          coolRangeHigh                       = findOptional[Int](obj, "coolRangeHigh"),
          coolRangeLow                        = findOptional[Int](obj, "coolRangeLow"),
          auxRuntimeAlert                     = findOptional[Int](obj, "auxRuntimeAlert"),
          auxOutdoorTempAlert                 = findOptional[Int](obj, "auxOutdoorTempAlert"),
          auxMaxOutdoorTemp                   = findOptional[Int](obj, "auxMaxOutdoorTemp"),
          auxRuntimeAlertNotify               = findOptional[Boolean](obj, "auxRuntimeAlertNotify"),
          auxOutdoorTempAlertNotify           = findOptional[Boolean](obj, "auxOutdoorTempAlertNotify"),
          auxRuntimeAlertNotifyTechnician     = findOptional[Boolean](obj, "auxRuntimeAlertNotifyTechnician"),
          auxOutdoorTempAlertNotifyTechnician = findOptional[Boolean](obj, "auxOutdoorTempAlertNotifyTechnician"),
          disablePreHeating                   = findOptional[Boolean](obj, "disablePreHeating"),
          disablePreCooling                   = findOptional[Boolean](obj, "disablePreCooling"),
          installerCodeRequired               = findOptional[Boolean](obj, "installerCodeRequired"),
          drAccept                            = findOptional[DRAccept.Entry](obj, "drAccept"),
          isRentalProperty                    = findOptional[Boolean](obj, "isRentalProperty"),
          useZoneController                   = findOptional[Boolean](obj, "useZoneController"),
          randomStartDelayCool                = findOptional[Int](obj, "randomStartDelayCool"),
          randomStartDelayHeat                = findOptional[Int](obj, "randomStartDelayHeat"),
          humidityHighAlert                   = findOptional[Int](obj, "humidityHighAlert"),
          humidityLowAlert                    = findOptional[Int](obj, "humidityLowAlert"),
          disableHeatPumpAlerts               = findOptional[Boolean](obj, "disableHeatPumpAlerts"),
          disableAlertsOnIdt                  = findOptional[Boolean](obj, "disableAlertsOnIdt"),
          humidityAlertNotify                 = findOptional[Boolean](obj, "humidityAlertNotify"),
          humidityAlertNotifyTechnician       = findOptional[Boolean](obj, "humidityAlertNotifyTechnician"),
          tempAlertNotify                     = findOptional[Boolean](obj, "tempAlertNotify"),
          tempAlertNotifyTechnician           = findOptional[Boolean](obj, "tempAlertNotifyTechnician"),
          monthlyElectricityBillLimit         = findOptional[Int](obj, "monthlyElectricityBillLimit"),
          enableElectricityBillAlert          = findOptional[Boolean](obj, "enableElectricityBillAlert"),
          enableProjectedElectricityBillAlert = findOptional[Boolean](obj, "enableProjectedElectricityBillAlert"),
          electricityBillingDayOfMonth        = findOptional[Int](obj, "electricityBillingDayOfMonth"),
          electricityBillCycleMonths          = findOptional[Int](obj, "electricityBillCycleMonths"),
          electricityBillStartMonth           = findOptional[Int](obj, "electricityBillStartMonth"),
          ventilatorMinOnTimeHome             = findOptional[Int](obj, "ventilatorMinOnTimeHome"),
          ventilatorMinOnTimeAway             = findOptional[Int](obj, "ventilatorMinOnTimeAway"),
          backlightOffDuringSleep             = findOptional[Boolean](obj, "backlightOffDuringSleep"),
          autoAway                            = findOptional[Boolean](obj, "autoAway"),
          smartCirculation                    = findOptional[Boolean](obj, "smartCirculation"),
          followMeComfort                     = findOptional[Boolean](obj, "followMeComfort"),
          isVentilatorTimerOn                 = findOptional[Boolean](obj, "isVentilatorTimerOn"),
          hasUVFilter                         = findOptional[Boolean](obj, "hasUVFilter"),
          coolingLockout                      = findOptional[Boolean](obj, "coolingLockout"),
          ventilatorFreeCooling               = findOptional[Boolean](obj, "ventilatorFreeCooling"),
          dehumidifyWhenHeating               = findOptional[Boolean](obj, "dehumidifyWhenHeating"),
          ventilatorDehumidify                = findOptional[Boolean](obj, "ventilatorDehumidify"),
          groupRef                            = findOptional[String](obj, "groupRef"),
          groupName                           = findOptional[String](obj, "groupName"),
          groupSetting                        = findOptional[Int](obj, "groupSetting")
        )
      }
      case _ ⇒ deserializationError(s"Invalid thermostat settings message received: ${json}")
    }

    def write(obj : ThermostatSettingsModification) : JsValue = {
      val m = scala.collection.mutable.Map.empty[String, JsValue]

      obj.hvacMode.foreach { v ⇒ m += (("hvacMode", v.toJson)) }
      obj.lastServiceDate.foreach { v ⇒ m += (("lastServiceDate", v.toJson)) }
      obj.serviceRemindMe.foreach { v ⇒ m += (("serviceRemindMe", v.toJson)) }
      obj.monthsBetweenService.foreach { v ⇒ m += (("monthsBetweenService", v.toJson)) }
      obj.remindMeDate.foreach { v ⇒ m += (("remindMeDate", v.toJson)) }
      obj.vent.foreach { v ⇒ m += (("vent", v.toJson)) }
      obj.ventilatorMinOnTime.foreach { v ⇒ m += (("ventilatorMinOnTime", v.toJson)) }
      obj.serviceRemindTechnician.foreach { v ⇒ m += (("serviceRemindTechnician", v.toJson)) }
      obj.eiLocation.foreach { v ⇒ m += (("eiLocation", v.toJson)) }
      obj.coldTempAlert.foreach { v ⇒ m += (("coldTempAlert", v.toJson)) }
      obj.coldTempAlertEnabled.foreach { v ⇒ m += (("coldTempAlertEnabled", v.toJson)) }
      obj.hotTempAlert.foreach { v ⇒ m += (("hotTempAlert", v.toJson)) }
      obj.hotTempAlertEnabled.foreach { v ⇒ m += (("hotTempAlertEnabled", v.toJson)) }
      obj.maxSetBack.foreach { v ⇒ m += (("maxSetBack", v.toJson)) }
      obj.maxSetForward.foreach { v ⇒ m += (("maxSetForward", v.toJson)) }
      obj.quickSaveSetBack.foreach { v ⇒ m += (("quickSaveSetBack", v.toJson)) }
      obj.quickSaveSetForward.foreach { v ⇒ m += (("quickSaveSetForward", v.toJson)) }
      obj.condensationAvoid.foreach { v ⇒ m += (("condensationAvoid", v.toJson)) }
      obj.useCelsius.foreach { v ⇒ m += (("useCelsius", v.toJson)) }
      obj.useTimeFormat12.foreach { v ⇒ m += (("useTimeFormat12", v.toJson)) }
      obj.locale.foreach { v ⇒ m += (("locale", v.toJson)) }
      obj.humidity.foreach { v ⇒ m += (("humidity", v.toJson)) }
      obj.humidifierMode.foreach { v ⇒ m += (("humidifierMode", v.toJson)) }
      obj.backlightOnIntensity.foreach { v ⇒ m += (("backlightOnIntensity", v.toJson)) }
      obj.backlightSleepIntensity.foreach { v ⇒ m += (("backlightSleepIntensity", v.toJson)) }
      obj.backlightOffTime.foreach { v ⇒ m += (("backlightOffTime", v.toJson)) }
      obj.compressorProtectionMinTime.foreach { v ⇒ m += (("compressorProtectionMinTime", v.toJson)) }
      obj.compressorProtectionMinTemp.foreach { v ⇒ m += (("compressorProtectionMinTemp", v.toJson)) }
      obj.stage1HeatingDifferentialTemp.foreach { v ⇒ m += (("stage1HeatingDifferentialTemp", v.toJson)) }
      obj.stage1CoolingDifferentialTemp.foreach { v ⇒ m += (("stage1CoolingDifferentialTemp", v.toJson)) }
      obj.stage1HeatingDissipationTime.foreach { v ⇒ m += (("stage1HeatingDissipationTime", v.toJson)) }
      obj.stage1CoolingDissipationTime.foreach { v ⇒ m += (("stage1CoolingDissipationTime", v.toJson)) }
      obj.heatPumpReversalOnCool.foreach { v ⇒ m += (("heatPumpReversalOnCool", v.toJson)) }
      obj.fanControlRequired.foreach { v ⇒ m += (("fanControlRequired", v.toJson)) }
      obj.fanMinOnTime.foreach { v ⇒ m += (("fanMinOnTime", v.toJson)) }
      obj.heatCoolMinDelta.foreach { v ⇒ m += (("heatCoolMinDelta", v.toJson)) }
      obj.tempCorrection.foreach { v ⇒ m += (("tempCorrection", v.toJson)) }
      obj.holdAction.foreach { v ⇒ m += (("holdAction", v.toJson)) }
      obj.dehumidifierMode.foreach { v ⇒ m += (("dehumidifierMode", v.toJson)) }
      obj.dehumidifierLevel.foreach { v ⇒ m += (("dehumidifierLevel", v.toJson)) }
      obj.dehumidifyWithAC.foreach { v ⇒ m += (("dehumidifyWithAC", v.toJson)) }
      obj.dehumidifyOvercoolOffset.foreach { v ⇒ m += (("dehumidifyOvercoolOffset", v.toJson)) }
      obj.autoHeatCoolFeatureEnabled.foreach { v ⇒ m += (("autoHeatCoolFeatureEnabled", v.toJson)) }
      obj.wifiOfflineAlert.foreach { v ⇒ m += (("wifiOfflineAlert", v.toJson)) }
      obj.heatRangeHigh.foreach { v ⇒ m += (("heatRangeHigh", v.toJson)) }
      obj.heatRangeLow.foreach { v ⇒ m += (("heatRangeLow", v.toJson)) }
      obj.coolRangeHigh.foreach { v ⇒ m += (("coolRangeHigh", v.toJson)) }
      obj.coolRangeLow.foreach { v ⇒ m += (("coolRangeLow", v.toJson)) }
      obj.auxRuntimeAlert.foreach { v ⇒ m += (("auxRuntimeAlert", v.toJson)) }
      obj.auxOutdoorTempAlert.foreach { v ⇒ m += (("auxOutdoorTempAlert", v.toJson)) }
      obj.auxMaxOutdoorTemp.foreach { v ⇒ m += (("auxMaxOutdoorTemp", v.toJson)) }
      obj.auxRuntimeAlertNotify.foreach { v ⇒ m += (("auxRuntimeAlertNotify", v.toJson)) }
      obj.auxOutdoorTempAlertNotify.foreach { v ⇒ m += (("auxOutdoorTempAlertNotify", v.toJson)) }
      obj.auxRuntimeAlertNotifyTechnician.foreach { v ⇒ m += (("auxRuntimeAlertNotifyTechnician", v.toJson)) }
      obj.auxOutdoorTempAlertNotifyTechnician.foreach { v ⇒ m += (("auxOutdoorTempAlertNotifyTechnician", v.toJson)) }
      obj.disablePreHeating.foreach { v ⇒ m += (("disablePreHeating", v.toJson)) }
      obj.disablePreCooling.foreach { v ⇒ m += (("disablePreCooling", v.toJson)) }
      obj.installerCodeRequired.foreach { v ⇒ m += (("installerCodeRequired", v.toJson)) }
      obj.drAccept.foreach { v ⇒ m += (("drAccept", v.toJson)) }
      obj.isRentalProperty.foreach { v ⇒ m += (("isRentalProperty", v.toJson)) }
      obj.useZoneController.foreach { v ⇒ m += (("useZoneController", v.toJson)) }
      obj.randomStartDelayCool.foreach { v ⇒ m += (("randomStartDelayCool", v.toJson)) }
      obj.randomStartDelayHeat.foreach { v ⇒ m += (("randomStartDelayHeat", v.toJson)) }
      obj.humidityHighAlert.foreach { v ⇒ m += (("humidityHighAlert", v.toJson)) }
      obj.humidityLowAlert.foreach { v ⇒ m += (("humidityLowAlert", v.toJson)) }
      obj.disableHeatPumpAlerts.foreach { v ⇒ m += (("disableHeatPumpAlerts", v.toJson)) }
      obj.disableAlertsOnIdt.foreach { v ⇒ m += (("disableAlertsOnIdt", v.toJson)) }
      obj.humidityAlertNotify.foreach { v ⇒ m += (("humidityAlertNotify", v.toJson)) }
      obj.humidityAlertNotifyTechnician.foreach { v ⇒ m += (("humidityAlertNotifyTechnician", v.toJson)) }
      obj.tempAlertNotify.foreach { v ⇒ m += (("tempAlertNotify", v.toJson)) }
      obj.tempAlertNotifyTechnician.foreach { v ⇒ m += (("tempAlertNotifyTechnician", v.toJson)) }
      obj.monthlyElectricityBillLimit.foreach { v ⇒ m += (("monthlyElectricityBillLimit", v.toJson)) }
      obj.enableElectricityBillAlert.foreach { v ⇒ m += (("enableElectricityBillAlert", v.toJson)) }
      obj.enableProjectedElectricityBillAlert.foreach { v ⇒ m += (("enableProjectedElectricityBillAlert", v.toJson)) }
      obj.electricityBillingDayOfMonth.foreach { v ⇒ m += (("electricityBillingDayOfMonth", v.toJson)) }
      obj.electricityBillCycleMonths.foreach { v ⇒ m += (("electricityBillCycleMonths", v.toJson)) }
      obj.electricityBillStartMonth.foreach { v ⇒ m += (("electricityBillStartMonth", v.toJson)) }
      obj.ventilatorMinOnTimeHome.foreach { v ⇒ m += (("ventilatorMinOnTimeHome", v.toJson)) }
      obj.ventilatorMinOnTimeAway.foreach { v ⇒ m += (("ventilatorMinOnTimeAway", v.toJson)) }
      obj.backlightOffDuringSleep.foreach { v ⇒ m += (("backlightOffDuringSleep", v.toJson)) }
      obj.autoAway.foreach { v ⇒ m += (("autoAway", v.toJson)) }
      obj.smartCirculation.foreach { v ⇒ m += (("smartCirculation", v.toJson)) }
      obj.followMeComfort.foreach { v ⇒ m += (("followMeComfort", v.toJson)) }
      obj.isVentilatorTimerOn.foreach { v ⇒ m += (("isVentilatorTimerOn", v.toJson)) }
      obj.hasUVFilter.foreach { v ⇒ m += (("hasUVFilter", v.toJson)) }
      obj.coolingLockout.foreach { v ⇒ m += (("coolingLockout", v.toJson)) }
      obj.ventilatorFreeCooling.foreach { v ⇒ m += (("ventilatorFreeCooling", v.toJson)) }
      obj.dehumidifyWhenHeating.foreach { v ⇒ m += (("dehumidifyWhenHeating", v.toJson)) }
      obj.ventilatorDehumidify.foreach { v ⇒ m += (("ventilatorDehumidify", v.toJson)) }
      obj.groupRef.foreach { v ⇒ m += (("groupRef", v.toJson)) }
      obj.groupName.foreach { v ⇒ m += (("groupName", v.toJson)) }
      obj.groupSetting.foreach { v ⇒ m += (("groupSetting", v.toJson)) }

      JsObject(m.toMap)
    }
  }
}





