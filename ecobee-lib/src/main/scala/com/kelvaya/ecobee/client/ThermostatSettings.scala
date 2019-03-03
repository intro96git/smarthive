package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.Time.FullDate

/** All settings that a Thermostat may use
  *
  * @param hvacMode The current HVAC mode the thermostat is in.
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
  * @param coolStages The number of cool stages the connected HVAC equipment supports.
  * @param heatStages The number of heat stages the connected HVAC equipment supports.
  * @param maxSetBack The maximum automated set point set back offset allowed in degrees.
  * @param maxSetForward The maximum automated set point set forward offset allowed in degrees.
  * @param quickSaveSetBack The set point set back offset, in degrees, configured for a quick save event.
  * @param quickSaveSetForward The set point set forward offset, in degrees, configured for a quick save event.
  * @param hasHeatPump Whether the thermostat is controlling a heat pump.
  * @param hasForcedAir Whether the thermostat is controlling a forced air furnace.
  * @param hasBoiler Whether the thermostat is controlling a boiler.
  * @param hasHumidifier Whether the thermostat is controlling a humidifier.
  * @param hasErv Whether the thermostat is controlling an energy recovery ventilator.
  * @param hasHrv Whether the thermostat is controlling a heat recovery ventilator.
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
  * @param heatPumpGroundWater Whether the Thermostat uses a geothermal / ground source heat pump.
  * @param hasElectric Whether the thermostat is connected to an electric HVAC system.
  * @param hasDehumidifier Whether the thermostat is connected to a dehumidifier. If true or dehumidifyOvercoolOffset > 0 then allow setting dehumidifierMode and dehumidifierLevel.
  * @param dehumidifierMode The dehumidifier mode.If set to off then the dehumidifier will not run, nor will the AC overcool run.
  * @param dehumidifierLevel The dehumidification set point in percentage.
  * @param dehumidifyWithAC Whether the thermostat should use AC overcool to dehumidify. When set to true a postive integer value must be supplied for dehumidifyOvercoolOffset.
  * @param dehumidifyOvercoolOffset Whether the thermostat should use AC overcool to dehumidify and what that temperature offset should be. A value of 0 means this feature is disabled. Value represents the value in F to subract from the current set point. Values should be in the range 0 - 50 and be divisible by 5.
  * @param autoHeatCoolFeatureEnabled If enabled, allows the Thermostat to be put in HVACAuto mode.
  * @param wifiOfflineAlert Whether the alert for when wifi is offline is enabled.
  * @param heatMinTemp The minimum heat set point allowed by the thermostat firmware.
  * @param heatMaxTemp The maximum heat set point allowed by the thermostat firmware.
  * @param coolMinTemp The minimum cool set point allowed by the thermostat firmware.
  * @param coolMaxTemp The maximum cool set point allowed by the thermostat firmware.
  * @param heatRangeHigh The maximum heat set point configured by the user's preferences.
  * @param heatRangeLow The minimum heat set point configured by the user's preferences.
  * @param coolRangeHigh The maximum cool set point configured by the user's preferences.
  * @param coolRangeLow The minimum heat set point configured by the user's preferences.
  * @param userAccessCode The user access code value for this thermostat.
  * @param userAccessSetting The integer representation of the user access settings.
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
  * @param ventilatorType The type of ventilator present for the Thermostat.
  * @param isVentilatorTimerOn This Boolean field represents whether the ventilator timer is on or off. The default value is false. If set to true the ventilatorOffDateTime is set to now() + 20 minutes. If set to false the ventilatorOffDateTime is set to it's default value.
  * @param ventilatorOffDateTime This read-only field represents the Date and Time the ventilator will run until.
  * @param hasUVFilter This Boolean field represents whether the HVAC system has a UV filter.
  * @param coolingLockout This field represents whether to permit the cooling to operate when the Outdoor temeperature is under a specific threshold, currently 55F. The default value is false.
  * @param ventilatorFreeCooling Whether to use the ventilator to dehumidify when climate or calendar event indicates the owner is home.
  * @param dehumidifyWhenHeating This field represents whether to permit dehumidifer to operate when the heating is running.
  * @param ventilatorDehumidify This field represents whether or not to allow dehumification when cooling.
  * @param groupRef The unique reference to the group this thermostat belongs to.
  * @param groupName The name of the the group this thermostat belongs to.
  * @param groupSetting The setting value for the group this thermostat belongs to.
  */
case class ThermostatSettings(
    hvacMode :                            HVACMode.Entry,
    lastServiceDate :                     FullDate,
    serviceRemindMe :                     Boolean,
    monthsBetweenService :                Int,
    remindMeDate :                        FullDate,
    vent :                                VentilatorMode.Entry,
    ventilatorMinOnTime :                 Int,
    serviceRemindTechnician :             Boolean,
    eiLocation :                          String,
    coldTempAlert :                       Int,
    coldTempAlertEnabled :                Boolean,
    hotTempAlert :                        Int,
    hotTempAlertEnabled :                 Boolean,
    coolStages :                          Int,
    heatStages :                          Int,
    maxSetBack :                          Int,
    maxSetForward :                       Int,
    quickSaveSetBack :                    Int,
    quickSaveSetForward :                 Int,
    hasHeatPump :                         Boolean,
    hasForcedAir :                        Boolean,
    hasBoiler :                           Boolean,
    hasHumidifier :                       Boolean,
    hasErv :                              Boolean,
    hasHrv :                              Boolean,
    condensationAvoid :                   Boolean,
    useCelsius :                          Boolean,
    useTimeFormat12 :                     Boolean,
    locale :                              String,
    humidity :                            String,
    humidifierMode :                      HumidifierMode.Entry,
    backlightOnIntensity :                Int,
    backlightSleepIntensity :             Int,
    backlightOffTime :                    Int,
    compressorProtectionMinTime :         Int,
    compressorProtectionMinTemp :         Int,
    stage1HeatingDifferentialTemp :       Int,
    stage1CoolingDifferentialTemp :       Int,
    stage1HeatingDissipationTime :        Int,
    stage1CoolingDissipationTime :        Int,
    heatPumpReversalOnCool :              Boolean,
    fanControlRequired :                  Boolean,
    fanMinOnTime :                        Int,
    heatCoolMinDelta :                    Int,
    tempCorrection :                      Int,
    holdAction :                          HoldAction.Entry,
    heatPumpGroundWater :                 Boolean,
    hasElectric :                         Boolean,
    hasDehumidifier :                     Boolean,
    dehumidifierMode :                    DehumidifierMode.Entry,
    dehumidifierLevel :                   Int,
    dehumidifyWithAC :                    Boolean,
    dehumidifyOvercoolOffset :            Int,
    autoHeatCoolFeatureEnabled :          Boolean,
    wifiOfflineAlert :                    Boolean,
    heatMinTemp :                         Int,
    heatMaxTemp :                         Int,
    coolMinTemp :                         Int,
    coolMaxTemp :                         Int,
    heatRangeHigh :                       Int,
    heatRangeLow :                        Int,
    coolRangeHigh :                       Int,
    coolRangeLow :                        Int,
    userAccessCode :                      String,
    userAccessSetting :                   Int,
    auxRuntimeAlert :                     Int,
    auxOutdoorTempAlert :                 Int,
    auxMaxOutdoorTemp :                   Int,
    auxRuntimeAlertNotify :               Boolean,
    auxOutdoorTempAlertNotify :           Boolean,
    auxRuntimeAlertNotifyTechnician :     Boolean,
    auxOutdoorTempAlertNotifyTechnician : Boolean,
    disablePreHeating :                   Boolean,
    disablePreCooling :                   Boolean,
    installerCodeRequired :               Boolean,
    drAccept :                            DRAccept.Entry,
    isRentalProperty :                    Boolean,
    useZoneController :                   Boolean,
    randomStartDelayCool :                Int,
    randomStartDelayHeat :                Int,
    humidityHighAlert :                   Int,
    humidityLowAlert :                    Int,
    disableHeatPumpAlerts :               Boolean,
    disableAlertsOnIdt :                  Boolean,
    humidityAlertNotify :                 Boolean,
    humidityAlertNotifyTechnician :       Boolean,
    tempAlertNotify :                     Boolean,
    tempAlertNotifyTechnician :           Boolean,
    monthlyElectricityBillLimit :         Int,
    enableElectricityBillAlert :          Boolean,
    enableProjectedElectricityBillAlert : Boolean,
    electricityBillingDayOfMonth :        Int,
    electricityBillCycleMonths :          Int,
    electricityBillStartMonth :           Int,
    ventilatorMinOnTimeHome :             Int,
    ventilatorMinOnTimeAway :             Int,
    backlightOffDuringSleep :             Boolean,
    autoAway :                            Boolean,
    smartCirculation :                    Boolean,
    followMeComfort :                     Boolean,
    ventilatorType :                      VentilatorType.Entry,
    isVentilatorTimerOn :                 Boolean,
    ventilatorOffDateTime :               FullDate,
    hasUVFilter :                         Boolean,
    coolingLockout :                      Boolean,
    ventilatorFreeCooling :               Boolean,
    dehumidifyWhenHeating :               Boolean,
    ventilatorDehumidify :                Boolean,
    groupRef :                            String,
    groupName :                           String,
    groupSetting :                        Int
)


object ThermostatSettings extends SprayImplicits {

  implicit object ThermostatSettingsFormat extends RootJsonFormat[ThermostatSettings] {
    def read(json: JsValue): ThermostatSettings = json match {
      case obj : JsObject => {
        val fields = obj.fields.withDefault { k : String => JsNull }
        ThermostatSettings(
          hvacMode = fields("hvacMode").convertTo[HVACMode.Entry],
          lastServiceDate = fields("lastServiceDate").convertTo[FullDate],
          serviceRemindMe = fields("serviceRemindMe").convertTo[Boolean],
          monthsBetweenService = fields("obj.monthsBetweenService").convertTo[Int],
          remindMeDate = fields("remindMeDate").convertTo[FullDate],
          vent = fields("vent").convertTo[VentilatorMode.Entry],
          ventilatorMinOnTime = fields("ventilatorMinOnTime").convertTo[Int],
          serviceRemindTechnician = fields("serviceRemindTechnician").convertTo[Boolean],
          eiLocation = fields("eiLocation").convertTo[String],
          coldTempAlert = fields("coldTempAlert").convertTo[Int],
          coldTempAlertEnabled = fields("coldTempAlertEnabled").convertTo[Boolean],
          hotTempAlert = fields("hotTempAlert").convertTo[Int],
          hotTempAlertEnabled = fields("hotTempAlertEnabled").convertTo[Boolean],
          coolStages = fields("coolStages").convertTo[Int],
          heatStages = fields("heatStages").convertTo[Int],
          maxSetBack = fields("maxSetBack").convertTo[Int],
          maxSetForward = fields("maxSetForward").convertTo[Int],
          quickSaveSetBack = fields("quickSaveSetBack").convertTo[Int],
          quickSaveSetForward = fields("quickSaveSetForward").convertTo[Int],
          hasHeatPump = fields("hasHeatPump").convertTo[Boolean],
          hasForcedAir = fields("hasForcedAir").convertTo[Boolean],
          hasBoiler = fields("hasBoiler").convertTo[Boolean],
          hasHumidifier = fields("hasHumidifier").convertTo[Boolean],
          hasErv = fields("hasErv").convertTo[Boolean],
          hasHrv = fields("hasHrv").convertTo[Boolean],
          condensationAvoid = fields("condensationAvoid").convertTo[Boolean],
          useCelsius = fields("useCelsius").convertTo[Boolean],
          useTimeFormat12 = fields("useTimeFormat12").convertTo[Boolean],
          locale = fields("locale").convertTo[String],
          humidity = fields("humidity").convertTo[String],
          humidifierMode = fields("humidifierMode").convertTo[HumidifierMode.Entry],
          backlightOnIntensity = fields("backlightOnIntensity").convertTo[Int],
          backlightSleepIntensity = fields("backlightSleepIntensity").convertTo[Int],
          backlightOffTime = fields("backlightOffTime").convertTo[Int],
          compressorProtectionMinTime = fields("compressorProtectionMinTime").convertTo[Int],
          compressorProtectionMinTemp = fields("compressorProtectionMinTemp").convertTo[Int],
          stage1HeatingDifferentialTemp = fields("stage1HeatingDifferentialTemp").convertTo[Int],
          stage1CoolingDifferentialTemp = fields("stage1CoolingDifferentialTemp").convertTo[Int],
          stage1HeatingDissipationTime = fields("stage1HeatingDissipationTime").convertTo[Int],
          stage1CoolingDissipationTime = fields("stage1CoolingDissipationTime").convertTo[Int],
          heatPumpReversalOnCool = fields("heatPumpReversalOnCool").convertTo[Boolean],
          fanControlRequired = fields("fanControlRequired").convertTo[Boolean],
          fanMinOnTime = fields("fanMinOnTime").convertTo[Int],
          heatCoolMinDelta = fields("heatCoolMinDelta").convertTo[Int],
          tempCorrection = fields("tempCorrection").convertTo[Int],
          holdAction = fields("holdAction").convertTo[HoldAction.Entry],
          heatPumpGroundWater = fields("heatPumpGroundWater").convertTo[Boolean],
          hasElectric = fields("hasElectric").convertTo[Boolean],
          hasDehumidifier = fields("hasDehumidifier").convertTo[Boolean],
          dehumidifierMode = fields("dehumidifierMode").convertTo[DehumidifierMode.Entry],
          dehumidifierLevel = fields("dehumidifierLevel").convertTo[Int],
          dehumidifyWithAC = fields("dehumidifyWithAC").convertTo[Boolean],
          dehumidifyOvercoolOffset = fields("dehumidifyOvercoolOffset").convertTo[Int],
          autoHeatCoolFeatureEnabled = fields("autoHeatCoolFeatureEnabled").convertTo[Boolean],
          wifiOfflineAlert = fields("wifiOfflineAlert").convertTo[Boolean],
          heatMinTemp = fields("heatMinTemp").convertTo[Int],
          heatMaxTemp = fields("heatMaxTemp").convertTo[Int],
          coolMinTemp = fields("coolMinTemp").convertTo[Int],
          coolMaxTemp = fields("coolMaxTemp").convertTo[Int],
          heatRangeHigh = fields("heatRangeHigh").convertTo[Int],
          heatRangeLow = fields("heatRangeLow").convertTo[Int],
          coolRangeHigh = fields("coolRangeHigh").convertTo[Int],
          coolRangeLow = fields("coolRangeLow").convertTo[Int],
          userAccessCode = fields("userAccessCode").convertTo[String],
          userAccessSetting = fields("userAccessSetting").convertTo[Int],
          auxRuntimeAlert = fields("auxRuntimeAlert").convertTo[Int],
          auxOutdoorTempAlert = fields("auxOutdoorTempAlert").convertTo[Int],
          auxMaxOutdoorTemp = fields("auxMaxOutdoorTemp").convertTo[Int],
          auxRuntimeAlertNotify = fields("auxRuntimeAlertNotify").convertTo[Boolean],
          auxOutdoorTempAlertNotify = fields("auxOutdoorTempAlertNotify").convertTo[Boolean],
          auxRuntimeAlertNotifyTechnician = fields("auxRuntimeAlertNotifyTechnician").convertTo[Boolean],
          auxOutdoorTempAlertNotifyTechnician = fields("auxOutdoorTempAlertNotifyTechnician").convertTo[Boolean],
          disablePreHeating = fields("disablePreHeating").convertTo[Boolean],
          disablePreCooling = fields("disablePreCooling").convertTo[Boolean],
          installerCodeRequired = fields("installerCodeRequired").convertTo[Boolean],
          drAccept = fields("drAccept").convertTo[DRAccept.Entry],
          isRentalProperty = fields("isRentalProperty").convertTo[Boolean],
          useZoneController = fields("useZoneController").convertTo[Boolean],
          randomStartDelayCool = fields("randomStartDelayCool").convertTo[Int],
          randomStartDelayHeat = fields("randomStartDelayHeat").convertTo[Int],
          humidityHighAlert = fields("humidityHighAlert").convertTo[Int],
          humidityLowAlert = fields("humidityLowAlert").convertTo[Int],
          disableHeatPumpAlerts = fields("disableHeatPumpAlerts").convertTo[Boolean],
          disableAlertsOnIdt = fields("disableAlertsOnIdt").convertTo[Boolean],
          humidityAlertNotify = fields("humidityAlertNotify").convertTo[Boolean],
          humidityAlertNotifyTechnician = fields("humidityAlertNotifyTechnician").convertTo[Boolean],
          tempAlertNotify = fields("tempAlertNotify").convertTo[Boolean],
          tempAlertNotifyTechnician = fields("tempAlertNotifyTechnician").convertTo[Boolean],
          monthlyElectricityBillLimit = fields("monthlyElectricityBillLimit").convertTo[Int],
          enableElectricityBillAlert = fields("enableElectricityBillAlert").convertTo[Boolean],
          enableProjectedElectricityBillAlert = fields("enableProjectedElectricityBillAlert").convertTo[Boolean],
          electricityBillingDayOfMonth = fields("electricityBillingDayOfMonth").convertTo[Int],
          electricityBillCycleMonths = fields("electricityBillCycleMonths").convertTo[Int],
          electricityBillStartMonth = fields("electricityBillStartMonth").convertTo[Int],
          ventilatorMinOnTimeHome = fields("ventilatorMinOnTimeHome").convertTo[Int],
          ventilatorMinOnTimeAway = fields("ventilatorMinOnTimeAway").convertTo[Int],
          backlightOffDuringSleep = fields("backlightOffDuringSleep").convertTo[Boolean],
          autoAway = fields("autoAway").convertTo[Boolean],
          smartCirculation = fields("smartCirculation").convertTo[Boolean],
          followMeComfort = fields("followMeComfort").convertTo[Boolean],
          ventilatorType = fields("ventilatorType").convertTo[VentilatorType.Entry],
          isVentilatorTimerOn = fields("isVentilatorTimerOn").convertTo[Boolean],
          ventilatorOffDateTime = fields("ventilatorOffDateTime").convertTo[FullDate],
          hasUVFilter = fields("hasUVFilter").convertTo[Boolean],
          coolingLockout = fields("coolingLockout").convertTo[Boolean],
          ventilatorFreeCooling = fields("ventilatorFreeCooling").convertTo[Boolean],
          dehumidifyWhenHeating = fields("dehumidifyWhenHeating").convertTo[Boolean],
          ventilatorDehumidify = fields("ventilatorDehumidify").convertTo[Boolean],
          groupRef = fields("groupRef").convertTo[String],
          groupName = fields("groupName").convertTo[String],
          groupSetting = fields("groupSetting").convertTo[Int]
        )
      }
      case _ => deserializationError(s"Invalid thermostat settings message received: ${json}")
    }

    def write(obj: ThermostatSettings): JsValue = {
      JsObject(
        Map[String,JsValue](
          "hvacMode" -> obj.hvacMode.toJson,
          "lastServiceDate" -> obj.lastServiceDate.toJson,
          "serviceRemindMe" -> JsBoolean(obj.serviceRemindMe),
          "monthsBetweenService" -> JsNumber(obj.monthsBetweenService),
          "remindMeDate" -> obj.remindMeDate.toJson,
          "vent" -> obj.vent.toJson,
          "ventilatorMinOnTime" -> JsNumber(obj.ventilatorMinOnTime),
          "serviceRemindTechnician" -> JsBoolean(obj.serviceRemindTechnician),
          "eiLocation" -> JsString(obj.eiLocation),
          "coldTempAlert" -> JsNumber(obj.coldTempAlert),
          "coldTempAlertEnabled" -> JsBoolean(obj.coldTempAlertEnabled),
          "hotTempAlert" -> JsNumber(obj.hotTempAlert),
          "hotTempAlertEnabled" -> JsBoolean(obj.hotTempAlertEnabled),
          "coolStages" -> JsNumber(obj.coolStages),
          "heatStages" -> JsNumber(obj.heatStages),
          "maxSetBack" -> JsNumber(obj.maxSetBack),
          "maxSetForward" -> JsNumber(obj.maxSetForward),
          "quickSaveSetBack" -> JsNumber(obj.quickSaveSetBack),
          "quickSaveSetForward" -> JsNumber(obj.quickSaveSetForward),
          "hasHeatPump" -> JsBoolean(obj.hasHeatPump),
          "hasForcedAir" -> JsBoolean(obj.hasForcedAir),
          "hasBoiler" -> JsBoolean(obj.hasBoiler),
          "hasHumidifier" -> JsBoolean(obj.hasHumidifier),
          "hasErv" -> JsBoolean(obj.hasErv),
          "hasHrv" -> JsBoolean(obj.hasHrv),
          "condensationAvoid" -> JsBoolean(obj.condensationAvoid),
          "useCelsius" -> JsBoolean(obj.useCelsius),
          "useTimeFormat12" -> JsBoolean(obj.useTimeFormat12),
          "locale" -> JsString(obj.locale),
          "humidity" -> JsString(obj.humidity),
          "humidifierMode" -> obj.humidifierMode.toJson,
          "backlightOnIntensity" -> JsNumber(obj.backlightOnIntensity),
          "backlightSleepIntensity" -> JsNumber(obj.backlightSleepIntensity),
          "backlightOffTime" -> JsNumber(obj.backlightOffTime),
          "compressorProtectionMinTime" -> JsNumber(obj.compressorProtectionMinTime),
          "compressorProtectionMinTemp" -> JsNumber(obj.compressorProtectionMinTemp),
          "stage1HeatingDifferentialTemp" -> JsNumber(obj.stage1HeatingDifferentialTemp),
          "stage1CoolingDifferentialTemp" -> JsNumber(obj.stage1CoolingDifferentialTemp),
          "stage1HeatingDissipationTime" -> JsNumber(obj.stage1HeatingDissipationTime),
          "stage1CoolingDissipationTime" -> JsNumber(obj.stage1CoolingDissipationTime),
          "heatPumpReversalOnCool" -> JsBoolean(obj.heatPumpReversalOnCool),
          "fanControlRequired" -> JsBoolean(obj.fanControlRequired),
          "fanMinOnTime" -> JsNumber(obj.fanMinOnTime),
          "heatCoolMinDelta" -> JsNumber(obj.heatCoolMinDelta),
          "tempCorrection" -> JsNumber(obj.tempCorrection),
          "holdAction" -> obj.holdAction.toJson,
          "heatPumpGroundWater" -> JsBoolean(obj.heatPumpGroundWater),
          "hasElectric" -> JsBoolean(obj.hasElectric),
          "hasDehumidifier" -> JsBoolean(obj.hasDehumidifier),
          "dehumidifierMode" -> obj.dehumidifierMode.toJson,
          "dehumidifierLevel" -> JsNumber(obj.dehumidifierLevel),
          "dehumidifyWithAC" -> JsBoolean(obj.dehumidifyWithAC),
          "dehumidifyOvercoolOffset" -> JsNumber(obj.dehumidifyOvercoolOffset),
          "autoHeatCoolFeatureEnabled" -> JsBoolean(obj.autoHeatCoolFeatureEnabled),
          "wifiOfflineAlert" -> JsBoolean(obj.wifiOfflineAlert),
          "heatMinTemp" -> JsNumber(obj.heatMinTemp),
          "heatMaxTemp" -> JsNumber(obj.heatMaxTemp),
          "coolMinTemp" -> JsNumber(obj.coolMinTemp),
          "coolMaxTemp" -> JsNumber(obj.coolMaxTemp),
          "heatRangeHigh" -> JsNumber(obj.heatRangeHigh),
          "heatRangeLow" -> JsNumber(obj.heatRangeLow),
          "coolRangeHigh" -> JsNumber(obj.coolRangeHigh),
          "coolRangeLow" -> JsNumber(obj.coolRangeLow),
          "userAccessCode" -> JsString(obj.userAccessCode),
          "userAccessSetting" -> JsNumber(obj.userAccessSetting),
          "auxRuntimeAlert" -> JsNumber(obj.auxRuntimeAlert),
          "auxOutdoorTempAlert" -> JsNumber(obj.auxOutdoorTempAlert),
          "auxMaxOutdoorTemp" -> JsNumber(obj.auxMaxOutdoorTemp),
          "auxRuntimeAlertNotify" -> JsBoolean(obj.auxRuntimeAlertNotify),
          "auxOutdoorTempAlertNotify" -> JsBoolean(obj.auxOutdoorTempAlertNotify),
          "auxRuntimeAlertNotifyTechnician" -> JsBoolean(obj.auxRuntimeAlertNotifyTechnician),
          "auxOutdoorTempAlertNotifyTechnician" -> JsBoolean(obj.auxOutdoorTempAlertNotifyTechnician),
          "disablePreHeating" -> JsBoolean(obj.disablePreHeating),
          "disablePreCooling" -> JsBoolean(obj.disablePreCooling),
          "installerCodeRequired" -> JsBoolean(obj.installerCodeRequired),
          "drAccept" -> obj.drAccept.toJson,
          "isRentalProperty" -> JsBoolean(obj.isRentalProperty),
          "useZoneController" -> JsBoolean(obj.useZoneController),
          "randomStartDelayCool" -> JsNumber(obj.randomStartDelayCool),
          "randomStartDelayHeat" -> JsNumber(obj.randomStartDelayHeat),
          "humidityHighAlert" -> JsNumber(obj.humidityHighAlert),
          "humidityLowAlert" -> JsNumber(obj.humidityLowAlert),
          "disableHeatPumpAlerts" -> JsBoolean(obj.disableHeatPumpAlerts),
          "disableAlertsOnIdt" -> JsBoolean(obj.disableAlertsOnIdt),
          "humidityAlertNotify" -> JsBoolean(obj.humidityAlertNotify),
          "humidityAlertNotifyTechnician" -> JsBoolean(obj.humidityAlertNotifyTechnician),
          "tempAlertNotify" -> JsBoolean(obj.tempAlertNotify),
          "tempAlertNotifyTechnician" -> JsBoolean(obj.tempAlertNotifyTechnician),
          "monthlyElectricityBillLimit" -> JsNumber(obj.monthlyElectricityBillLimit),
          "enableElectricityBillAlert" -> JsBoolean(obj.enableElectricityBillAlert),
          "enableProjectedElectricityBillAlert" -> JsBoolean(obj.enableProjectedElectricityBillAlert),
          "electricityBillingDayOfMonth" -> JsNumber(obj.electricityBillingDayOfMonth),
          "electricityBillCycleMonths" -> JsNumber(obj.electricityBillCycleMonths),
          "electricityBillStartMonth" -> JsNumber(obj.electricityBillStartMonth),
          "ventilatorMinOnTimeHome" -> JsNumber(obj.ventilatorMinOnTimeHome),
          "ventilatorMinOnTimeAway" -> JsNumber(obj.ventilatorMinOnTimeAway),
          "backlightOffDuringSleep" -> JsBoolean(obj.backlightOffDuringSleep),
          "autoAway" -> JsBoolean(obj.autoAway),
          "smartCirculation" -> JsBoolean(obj.smartCirculation),
          "followMeComfort" -> JsBoolean(obj.followMeComfort),
          "ventilatorType" -> obj.ventilatorType.toJson,
          "isVentilatorTimerOn" -> JsBoolean(obj.isVentilatorTimerOn),
          "ventilatorOffDateTime" -> obj.ventilatorOffDateTime.toJson,
          "hasUVFilter" -> JsBoolean(obj.hasUVFilter),
          "coolingLockout" -> JsBoolean(obj.coolingLockout),
          "ventilatorFreeCooling" -> JsBoolean(obj.ventilatorFreeCooling),
          "dehumidifyWhenHeating" -> JsBoolean(obj.dehumidifyWhenHeating),
          "ventilatorDehumidify" -> JsBoolean(obj.ventilatorDehumidify),
          "groupRef" -> JsString(obj.groupRef),
          "groupName" -> JsString(obj.groupName),
          "groupSetting" -> JsNumber(obj.groupSetting),
        )
      )
    }
  }
}