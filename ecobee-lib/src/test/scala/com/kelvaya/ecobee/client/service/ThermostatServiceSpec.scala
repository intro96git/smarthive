package com.kelvaya.ecobee.client.service

import com.kelvaya.ecobee.test.BaseTestSpec
import com.kelvaya.ecobee.client.Client
import com.kelvaya.ecobee.config.Settings
import akka.http.scaladsl.model._
import com.kelvaya.ecobee.client.Request
import spray.json._
import spray.json.DefaultJsonProtocol._

class ThermostatServiceSpec extends BaseTestSpec {

  implicit lazy val settings = this.injector.instance[Settings]
  implicit lazy val exec = this.createTestExecutor(Map.empty)
  implicit lazy val client = new Client

  "The thermostat service" must "serialize requests correctly" in {

    val selection = Select(SelectType.Thermostats, includeRuntime=true)

    val service = new ThermostatService(selection)
    service.execute()

    // ############

    import monix.execution.Scheduler.Implicits.global

    val thermReq = ThermostatRequest(selection)
    val req: HttpRequest = thermReq.createRequest.runSyncUnsafe(scala.concurrent.duration.Duration("5 seconds"))
    req.method shouldBe HttpMethods.GET
    req.entity shouldBe HttpEntity.empty(Request.ContentTypeJson)
    req.uri.path shouldBe Uri.Path("/thermostat")


    val expectedSelectionQs = """{"selectionType":"thermostats","selectionMatch":"","includeRuntime":true}""".parseJson

    val qsSelection = req.uri.query().get("selection")
    qsSelection.value.parseJson shouldBe expectedSelectionQs

    val qsPage = req.uri.query().get("page")
    qsPage shouldBe None

    req.uri.query().size shouldBe 2

    // ############

    val thermReq2 = ThermostatRequest(selection, 1)
    val req2: HttpRequest = thermReq2.createRequest.runSyncUnsafe(scala.concurrent.duration.Duration("5 seconds"))

    val expectedPage2 = """{"page":1}""".parseJson

    val qsSelection2 = req2.uri.query().get("selection")
    qsSelection2.value.parseJson shouldBe expectedSelectionQs

    val qsPage2 = req2.uri.query().get("page")
    qsPage2.value.parseJson shouldBe expectedPage2

    req2.uri.query().size shouldBe 3
  }



  it must "parse a response" in {
    val response = """{
        "page": {
            "page": 1,
            "totalPages": 1,
            "pageSize": 1,
            "total": 1
        },
        "thermostatList": [
            {
              "identifier": "161775386723",
              "name": "testme",
              "thermostatRev": "110128234025",
              "isRegistered": true,
              "modelNumber": "Smart",
              "serialNumber": "1",
              "productCode": "1",
              "lastModified": "2011-01-28 23:40:25",
              "alerts": [],
              "settings": {
                "hvacMode": "heat",
                "lastServiceDate": "2011-01-28",
                "serviceRemindMe": false,
                "monthsBetweenService" : 12,
                "remindMeDate" : "2011-06-28",
                "vent" : "off",
                "ventilatorMinTime" : 0,
                "serviceRemindTechnician" : false,
                "eiLocation" : "In the back",
                "coldTempAlert" : 520,
                "coldTempAlertEnabled" : true,
                "hotTempAlert" : 920,
                "hotTempAlertEnabled" : true,
                "coolStages" : 2,
                "heatStages" : 1,
                "maxSetBack" : 1,
                "maxSetForward" : 0,
                "quickSaveSetBack" : 0,
                "quickSaveSetForward" : 0,
                "hasHeatPump" : false,
                "hasForcedAir" : false,
                "hasBoiler" : false,
                "hasHumidifier" : false,
                "hasErv" : false,
                "hasHrv" : false,
                "condensationAvoid" : false,
                "useCelsius" : false,
                "useTimeFormat12" : false,
                "locale" : "en",
                "humidity" : "20%",
                "humidifierMode" : "off",
                "backlightOnIntensity" : 5,
                "backlightSleepIntensity" : 0,
                "backlightOffTime" : 20,
                "soundTickVolume" : 1,
                "soundAlertVolume" : 5,
                "compressorProtectionMinTime" : 240,
                "compressorProtectionMinTemp" : 320,
                "stage1HeatingDifferentialTemp" : 2,
                "stage1CoolingDifferentialTemp" : 2,
                "stage1HeatingDissipationTime" : 30,
                "stage1CoolingDissipationTime" : 30,
                "heatPumpReversalOnCool" : false,
                "fanControlRequired" : false,
                "fanMinOnTime" : 5,
                "heatCoolMinDelta" : 4,
                "tempCorrection" : 0,
                "holdAction" : "nextPeriod",
                "heatPumpGroundWater" : false,
                "hasElectric" : true,
                "hasDehumidifier" : false,
                "dehumidifierMode" : "off",
                "dehumidifierLevel" : 0,
                "dehumidifyWithAC" : false,
                "dehumidifyOvercoolOffset" : 0,
                "autoHeatCoolFeatureEnabled" : false,
                "wifiOfflineAlert" : false,
                "heatMinTemp" : 520,
                "heatMaxTemp" : 800,
                "coolMinTemp" : 620,
                "coolMaxTemp" : 900,
                "heatRangeHigh" : 800,
                "heatRangeLow" : 550,
                "coolRangeHigh" : 880,
                "coolRangeLow" : 620,
                "userAccessCode" : "****",
                "userAccessSetting" : 0,
                "auxRuntimeAlert" : 600,
                "auxOutdoorTempAlert" : 320,
                "auxMaxOutdoorTemp" : 400,
                "auxRuntimeAlertNotify" : false,
                "auxOutdoorTempAlertNotify" : false,
                "auxRuntimeAlertNotifyTechnician" : false,
                "auxOutdoorTempAlertNotifyTechnician" : false,
                "disablePreHeating" : false,
                "disablePreCooling" : false,
                "installerCodeRequired" : false,
                "drAccept" : "never",
                "isRentalProperty" : false,
                "useZoneController" : false,
                "randomStartDelayCool" : 0,
                "randomStartDelayHeat" : 0,
                "humidityHighAlert" : 80,
                "humidityLowAlert" : 20,
                "disableHeatPumpAlerts" : false,
                "disableAlertsOnIdt" : false,
                "humidityAlertNotify" : true,
                "humidityAlertNotifyTechnician" : false,
                "tempAlertNotify" : false,
                "tempAlertNotifyTechnician" : false,
                "monthlyElectricityBillLimit" : 0,
                "enableElectricityBillAlert" : false,
                "enableProjectedElectricityBillAlert" : false,
                "electricityBillingDayOfMonth" : 0,
                "electricityBillCycleMonths" : 0,
                "electricityBillStartMonth" : 0,
                "ventilatorMinOnTimeHome" : 0,
                "ventilatorMinOnTimeAway" : 0,
                "backlightOffDuringSleep" : true,
                "autoAway" : true,
                "smartCirculation" : true,
                "followMeComfort" : false,
                "ventilatorType" : "none",
                "isVentilatorTimerOn" : false,
                "ventilatorOffDateTime" : "2014-01-01 00:00:00",
                "hasUVFilter" : false,
                "coolingLockout" : false,
                "ventilatorFreeCooling" : false,
                "dehumidifyWhenHeating" : false,
                "ventilatorDehumidify" : false,
                "groupRef" : "main",
                "groupName" : "Main Group",
                "groupSetting" : 0
              },
              "brand" : "mytest",
              "features" : "randomfeaturelist",
              "thermostatTime" : "2011-01-28 23:00:00",
              "utcTime" : "2011-01-29 04:00:00",
              "audio" : {
                "playbackVolume": 50,
                "microphoneEnabled" : false,
                "soundAlertVolume" : 1,
                "soundTickVolume" : 1,
                "voiceEngines" : [ { "testvoice" : false } ]
              },
              "runtime" : {
                "runtimeRev" : "1.2.3.4",
                "connected" : true,
                "firstConnected" : "2011-01-01 06:00:00",
                "connectDateTime" : "2011-01-01 09:12:11",
                "disconnectDateTime" : "2011-01-01 08:00:00",
                "lastModified" : "2011-01-30 20:00:00",
                "lastStatusModified" : "2011-02-01 12:00:00",
                "runtimeDate" : "2011-02-01",
                "runtimeInterval" : 200,
                "actualTemperature" : 685,
                "actualHumidity" : 50,
                "rawTemperature" : 685,
                "showIconMode" : 1,
                "desiredHeat" : 620,
                "desiredCool" : 700,
                "desiredHumidity" : 30,
                "desiredDehumidity" : 75,
                "desiredFanMode" : "auto",
                "desiredHeatRange" : [520,820],
                "desiredCoolRange" : [650,920]
              },
              "extendedRuntime" : {
                "lastReadingTimestamp" : "2010-09-23 14:23:45",
                "runtimeDate" : "2010-09-23",
                "runtimeInterval" : 23,
                "actualTemperature" : [ 720, 725, 720 ],
                "actualHumidity" : [ 50, 50, 55 ],
                "desiredHeat" : [ 620, 620, 620 ],
                "desiredCool" : [ 740, 740, 740 ],
                "desiredHumidity" : [ 30, 30, 30 ],
                "desiredDehumidity" : [ 70, 70, 70 ],
                "dmOffset" : [ 0, 0, 0 ],
                "hvacMode" : [ "compressorCoolStage10n", "economyCycle", "heatStage30n" ],
                "heatPump1" : [20, 21, 23],
                "heatPump2" : [1, 2, 3],
                "auxHeat1" : [0, 0, 0],
                "auxHeat2" : [0, 0, 0],
                "auxHeat3" : [0, 0, 0],
                "cool1" : [0, 0, 0],
                "cool2" : [0, 0, 0],
                "fan" : [ 5, 5, 5 ],
                "humidifier" : [ 0, 0, 0 ],
                "dehumidifier" : [ 0, 0, 0 ],
                "economizer" : [ 0, 0, 0 ],
                "ventilator" : [ 0, 0, 0 ],
                "currentElectricityBill" : 0,
                "projectedElectricityBill" : 0
              },
              "electricity" : { "devices" : [ { "name" : "powerco", "tiers" : [
                  { "name" : "Low", "consumption" : "0.000", "cost" : "0.05" },
                  { "name" : "High", "consumption" : "123.111", "cost" : "0.03" }
                ], "lastUpdate" : "2019-01-01 00:01:02", "cost" : [ "10.123" ], "consumption" : [ "4.444" ] } ]
              },
              "devices" : [ {"deviceId":1, "name":"One",
                "sensors":[ {
                  "name" : "Sensor1",
                  "manufacturer" : "Sony",
                  "model" : "XKCD",
                  "zone" : 1,
                  "sensorId" : 2353,
                  "type" : "temperature",
                  "usage" : "indoor",
                  "numberOfBits" : 0,
                  "bconstant" : 4,
                  "thermistorSize" : 2500,
                  "tempCorrection" : 5,
                  "gain" : 0,
                  "maxVoltage" : 10,
                  "multiplier" : 1,
                  "states" : [{
                    "maxValue" : 100,
                    "minValue" : 0,
                    "type" : "coolHigh",
                    "actions":[ { "type":"turnOnCool","sendAlert":false,"sendUpdate":false,"activationDelay":5,
                      "deactivationDelay":5,"minActionDuration":5,"heatAdjustTemp":0,"coolAdjustTemp":0,
                      "activateRelay":"","activateRelayOpen":false
                    }]
                  }]
                }],
                "outputs":[{
                  "name" : "output1",
                  "zone" : 1,
                  "outputId" : 123,
                  "type" : "zoneCool",
                  "sendUpdate" : false,
                  "activateClosed" : false,
                  "activationTime" : 5,
                  "deactivationTime" : 5
                }]
              }],
              "location" : {
                "timeZoneOffsetMinutes" : 180,
                "timeZone" : "America/Toronto",
                "isDaylightSaving" : true,
                "streetAddress" : "123 Main Street",
                "city" : "Springfield",
                "provinceState" : "IL",
                "country" : "USA",
                "postalCode" : "12345",
                "phoneNumber" : "",
                "mapCoordinates" : ""
              },
              "technician" : {
                "contractorRef" : "12345",
                "name" : "Joe Sixpack",
                "phone" : "123-456-7890",
                "streetAddress" : "1234 Main Street",
                "city" : "Springfield",
                "provinceState" : "IL",
                "country" : "USA",
                "postalCode" : "12345",
                "email" : "joesixpack@example.org",
                "web" : "https://www.example.org"
              },
              "utility" : {
                "name" : "Joe Sixpack",
                "phone" : "123-456-7890",
                "email" : "joesixpack@example.org",
                "web" : "https://www.example.org"
              },
              "management" : {
                "administrativeContact" : "Joe Bob",
                "billingContact" : "Mary Jane",
                "name" : "Ecobee Mgmt Company",
                "phone" : "555 1212",
                "email" : "support@example.com",
                "web" : "https://example.com",
                "showAlertIdt" : true,
                "showAlertWeb" : true
              },
              "weather" : {
                "timestamp" : "2019-09-01 20:03:30",
                "weatherStation" : "My House",
                "forecasts" : [{
                  "weatherSymbol": 1,
                  "dateTime": "2019-09-01 20:00:00",
                  "condition": "CLOUDY",
                  "temperature": 1,
                  "pressure": 1,
                  "relativeHumidity": 1,
                  "dewpoint": 1,
                  "visibility": 1,
                  "windSpeed": 1,
                  "windGust": 1,
                  "windDirection": "NNW",
                  "windBearing": 1,
                  "pop": 1,
                  "tempHigh": 1,
                  "tempLow": 1,
                  "sky": 1
                }]
              },
              "events" : [{
                "type" : "autoAway",
                "name" : "Event Three",
                "running" : true,
                "startDate" : "2019-09-01",
                "startTime" : "10:06:30",
                "endDate" : "2019-09-01",
                "endTime" : "11:06:30",
                "isOccupied" : true,
                "isCoolOff" : true,
                "isHeatOff" : true,
                "coolHoldTemp" : 700,
                "heatHoldTemp" : 700,
                "fan" : "auto",
                "vent" : "off",
                "ventilatorMinOnTime" : 1,
                "isOptional" : false,
                "isTemperatureRelative" : false,
                "coolRelativeTemp" : 700,
                "heatRelativeTemp" : 700,
                "isTemperatureAbsolute" : false,
                "dutyCyclePercentage" : 20,
                "fanMinOnTime" : 10,
                "occupiedSensorActive" : false,
                "unoccupiedSensorActive" : false,
                "drRampUpTemp" : 1,
                "drRampUpTime" : 1,
                "linkRef" : "Link Ref",
                "holdClimateRef" : "away"
              }],
              "houseDetails" : {
                "style" : "rowHouse",
                "size" : 3000,
                "numberOfFloors" : 2,
                "numberOfRooms" : 10,
                "numberOfOccupants" : 3,
                "age" : 115,
                "windowEfficiency" : 1
              },
              "program" : {
                "schedule" : [
                  ["home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
                  ["home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home"]
                ],
                "climates" : [{
                  "name" : "home",
                  "climateRef" : "home",
                  "isOccupied" : false
                }],
                "currentClimateRef" : "home"
              },
              "notificationSettings" : {
                "emailAddresses" : ["m@example.com", "o@example.com"],
                "emailNotificationsEnabled" : false,
                "equipment" : [{
                  "filterLastChanged" : "2019-05-01",
                  "filterLife" : 100,
                  "filterLifeUnits" : "hour",
                  "remindMeDate" : "2019-10-01",
                  "enabled" : true,
                  "type" : "airFilter",
                  "remindTechnician" : false
                }],
                "general" : [{ "enabled" : true, "type" : "temp", "remindTechnician" : false }],
                "limit" : [{
                  "limit" : 700,
                  "enabled" : true,
                  "type" : "highTemp",
                  "remindTechnican" : false
                }]
              },
              "version" : { "thermostatFirmwareVersion" : "5.4.1234 build 4321" },
              "securitySettings" : {
                "userAccessCode" : "****",
                "allUserAccess" : true,
                "programAccess" : true,
                "detailsAccess" : true,
                "quickSaveAccess" : true,
                "vacationAccess" : true
              },
              "remoteSensors" : [{
                "id" : "rs:100",
                "name" : "test",
                "type" : "thermostat",
                "code" : "AD3F",
                "inUse" : true,
                "capability" : [
                  { "id" : "1", "type" : "temperature", "value" : "800"},
                  { "id" : "2", "type" : "humidity", "value" : "50"}
                ]
              }],
              "equipmentStatus" : "fan:something,compCool1:something2"
            }
        ],
        "status": {
            "code": 200,
            "message": ""
        }
      }""".parseJson

    response.convertTo[ThermostatResponse]
  }
}
