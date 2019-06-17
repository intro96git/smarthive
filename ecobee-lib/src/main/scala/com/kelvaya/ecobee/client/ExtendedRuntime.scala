package com.kelvaya.ecobee.client

import com.kelvaya.util.Time

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.enum._

/** The extended runtime object contains the last three 5 minute interval values sent by the thermostat for the past 15 minutes of runtime.
  *
  * @param lastReadingTimestamp The timestamp of the last value read. This timestamp is updated at a 15 min interval by the thermostat. For the 1st value, it is timestamp - 10 mins, for the 2nd value it is timestamp - 5 mins.
  * @param runtimeDate The date of the last runtime reading.
  * @param runtimeInterval The last 5 minute interval which was updated by the thermostat telemetry update. Subtract 2 from this interval to obtain the beginning interval for the last 3 readings. Multiply by 5 mins to obtain the minutes of the day
  * @param actualTemperature The last three 5 minute actual temperature readings
  * @param actualHumidity The last three 5 minute actual humidity readings.
  * @param desiredHeat The last three 5 minute desired heat temperature readings.
  * @param desiredCool The last three 5 minute desired cool temperature readings.
  * @param desiredHumidity The last three 5 minute desired humidity readings.
  * @param desiredDehumidity The last three 5 minute desired de-humidification readings.
  * @param dmOffset The last three 5 minute desired Demand Management temeprature offsets. This value is Demand Management adjustment value which was applied by the thermostat. If the thermostat decided not to honour the adjustment, it will send 0 for the interval.
  * @param hvacMode The last three 5 minute HVAC Mode reading.
  * @param heatPump1 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the heat pump stage 1 runtime.
  * @param heatPump2 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the heat pump stage 2 runtime.
  * @param auxHeat1 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the auxiliary heat stage 1. If the thermostat does not have a heat pump, this is heat stage 1.
  * @param auxHeat2 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the auxiliary heat stage 2. If the thermostat does not have a heat pump, this is heat stage 2.
  * @param auxHeat3 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the heat stage 3 if the thermostat does not have a heat pump. Auxiliary stage 3 is not supported.
  * @param cool1 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the cooling stage 1.
  * @param cool2 The last three 5 minute HVAC Runtime values in seconds (0-300 seconds) per interval. This value corresponds to the cooling stage 2.
  * @param fan The last three 5 minute fan Runtime values in seconds (0-300 seconds) per interval.
  * @param humidifier The last three 5 minute humidifier Runtime values in seconds (0-300 seconds) per interval.
  * @param dehumidifier The last three 5 minute de-humidifier Runtime values in seconds (0-300 seconds) per interval.
  * @param economizer The last three 5 minute economizer Runtime values in seconds (0-300 seconds) per interval.
  * @param ventilator The last three 5 minute ventilator Runtime values in seconds (0-300 seconds) per interval.
  * @param currentElectricityBill The latest value of the current electricity bill as interpolated from the thermostat's readings from a paired electricity meter.
  * @param projectedElectricityBill The latest estimate of the projected electricity bill as interpolated from the thermostat's readings from a paired electricity meter.
  */
case class ExtendedRuntime(
    lastReadingTimestamp : Time.FullDate, runtimeDate : Time.DateOnly, runtimeInterval : Int, actualTemperature : Array[Int], actualHumidity : Array[Int],
    desiredHeat : Array[Int], desiredCool : Array[Int], desiredHumidity : Array[Int], desiredDehumidity : Array[Int], dmOffset : Array[Int],
    hvacMode : Array[ExtendedRuntime.RuntimeHVACMode.Entry], heatPump1 : Array[Int], heatPump2 : Array[Int], auxHeat1 : Array[Int], auxHeat2 : Array[Int], auxHeat3 : Array[Int],
    cool1 : Array[Int], cool2 : Array[Int], fan : Array[Int], humidifier : Array[Int], dehumidifier : Array[Int], economizer : Array[Int],
    ventilator : Array[Int], currentElectricityBill : Int, projectedElectricityBill : Int
) extends ReadonlyApiObject

object ExtendedRuntime {
  import SprayImplicits._

  object RuntimeHVACMode extends JsonStringEnum {
    val HeatStage1 = Val("heatStage10n")
    val HeatStage2 = Val("heatStage20n")
    val HeatStage3 = Val("heatStage30n")
    val HeatOff = Val("heatOff")
    val CompressorCoolStage1 = Val("compressorCoolStage10n")
    val CompressorCoolStage2 = Val("compressorCoolStage20n")
    val CompressorCoolOff = Val("compressorCoolOff")
    val CompressorHeatStage1 = Val("compressorHeatStage10n")
    val CmpressorHeatStage2 = Val("compressorHeatStage20n")
    val CompressorHeatOff = Val("compressorHeatOff")
    val EconomyCycle = Val("economyCycle")
  }

  implicit object Format extends RootJsonFormat[ExtendedRuntime] {

    def read(json: JsValue): ExtendedRuntime = json match {
      case obj : JsObject => ExtendedRuntime(
        lastReadingTimestamp = find[Time.FullDate](obj, "lastReadingTimestamp"),
        runtimeDate = find[Time.DateOnly](obj, "runtimeDate"),
        runtimeInterval = find[Int](obj, "runtimeInterval"),
        actualTemperature = find[Array[Int]](obj, "actualTemperature"),
        actualHumidity = find[Array[Int]](obj, "actualHumidity"),
        desiredHeat = find[Array[Int]](obj, "desiredHeat"),
        desiredCool = find[Array[Int]](obj, "desiredCool"),
        desiredHumidity = find[Array[Int]](obj, "desiredHumidity"),
        desiredDehumidity = find[Array[Int]](obj, "desiredDehumidity"),
        dmOffset = find[Array[Int]](obj, "dmOffset"),
        hvacMode = find[Array[RuntimeHVACMode.Entry]](obj, "hvacMode"),
        heatPump1 = find[Array[Int]](obj, "heatPump1"),
        heatPump2 = find[Array[Int]](obj, "heatPump2"),
        auxHeat1 = find[Array[Int]](obj, "auxHeat1"),
        auxHeat2 = find[Array[Int]](obj, "auxHeat2"),
        auxHeat3 = find[Array[Int]](obj, "auxHeat3"),
        cool1 = find[Array[Int]](obj, "cool1"),
        cool2 = find[Array[Int]](obj, "cool2"),
        fan = find[Array[Int]](obj, "fan"),
        humidifier = find[Array[Int]](obj, "humidifier"),
        dehumidifier = find[Array[Int]](obj, "dehumidifier"),
        economizer = find[Array[Int]](obj, "economizer"),
        ventilator = find[Array[Int]](obj, "ventilator"),
        currentElectricityBill = find[Int](obj, "currentElectricityBill"),
        projectedElectricityBill = find[Int](obj, "projectedElectricityBill")
      )
      case _ => deserializationError(s"Invalid Extended Runtime message received: ${json}")
    }

    def write(obj: ExtendedRuntime): JsValue = {
      val m = scala.collection.mutable.Map.empty[String, JsValue]

      m += (("lastReadingTimestamp", obj.lastReadingTimestamp.toJson))
      m += (("runtimeDate", obj.runtimeDate.toJson))
      m += (("runtimeInterval", obj.runtimeInterval.toJson))
      m += (("actualTemperature", obj.actualTemperature.toJson))
      m += (("actualHumidity", obj.actualHumidity.toJson))
      m += (("desiredHeat", obj.desiredHeat.toJson))
      m += (("desiredCool", obj.desiredCool.toJson))
      m += (("desiredHumidity", obj.desiredHumidity.toJson))
      m += (("desiredDehumidity", obj.desiredDehumidity.toJson))
      m += (("dmOffset", obj.dmOffset.toJson))
      m += (("hvacMode", obj.hvacMode.toJson))
      m += (("heatPump1", obj.heatPump1.toJson))
      m += (("heatPump2", obj.heatPump2.toJson))
      m += (("auxHeat1", obj.auxHeat1.toJson))
      m += (("auxHeat2", obj.auxHeat2.toJson))
      m += (("auxHeat3", obj.auxHeat3.toJson))
      m += (("cool1", obj.cool1.toJson))
      m += (("cool2", obj.cool2.toJson))
      m += (("fan", obj.fan.toJson))
      m += (("humidifier", obj.humidifier.toJson))
      m += (("dehumidifier", obj.dehumidifier.toJson))
      m += (("economizer", obj.economizer.toJson))
      m += (("ventilator", obj.ventilator.toJson))
      m += (("currentElectricityBill", obj.currentElectricityBill.toJson))
      m += (("projectedElectricityBill", obj.projectedElectricityBill.toJson))

      JsObject(m.toMap)
    }
  }
}
