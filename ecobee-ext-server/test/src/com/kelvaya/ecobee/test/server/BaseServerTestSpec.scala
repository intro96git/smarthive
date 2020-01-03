package com.kelvaya.ecobee.test.server

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.OptionValues
import org.scalatest.compatible.Assertion

import scala.language.implicitConversions

import com.kelvaya.ecobee.client._
import com.kelvaya.ecobee.client.Event.FanMode
import com.kelvaya.ecobee.client.service.EquipmentStatusListItem
import com.kelvaya.util.Time
import com.kelvaya.util.Time.FullDate

import zio.Task
import zio.ZIO

import akka.actor.ActorSystem
import com.kelvaya.util.Time.DateOnly
import com.kelvaya.ecobee.client.service.ThermostatResponse


trait BaseServerTestSpec extends FlatSpec with Matchers with OptionValues with ServerTestConstants {
  implicit val actorSys = ActorSystem("ecobee-ext-test")
}


trait ZioServerTestSpec extends BaseServerTestSpec {

  /** ZIO Runtime to use when evaluating effectful operations */
  val runtime = new TestRuntime

  implicit val clientSettings : ClientSettings.Service[Any] = runtime.environment.settings

  implicit def toZio(a : => Assertion) = Task(a)

  
  final def run[R >: ServerEnv](t : ZIO[R,Throwable,Assertion]) = this.runtime.unsafeRun(t.either).fold(
        e => throw e,
        s => s
      ) 

  protected def thermResponse(t : Thermostat*) = ThermostatResponse(t, Page(), Status(0,"Success"))


  protected def therm(
    identifier :           String,
    name :                 String = "name",
    thermostatRev :        String = "thermostatRev",
    isRegistered :         Boolean = true,
    modelNumber :          String = "model",
    brand :                String = "brand",
    features :             String = "features",
    lastModified :         FullDate = new FullDate(),
    thermostatTime :       FullDate = new FullDate(),
    utcTime :              FullDate = new FullDate(),
    audio :                Option[Audio] = None,
    alerts :               Option[Array[Alert]] = None,
    settings :             Option[ThermostatSettings] = None,
    runtime :              Option[ThermostatRuntime] = None,
    extendedRuntime :      Option[ExtendedRuntime] = None,
    electricity :          Option[Electricity] = None,
    devices :              Option[Array[Device]] = None,
    location :             Option[Location] = None,
    technician :           Option[Technician] = None,
    utility :              Option[Utility] = None,
    management :           Option[Management] = None,
    weather :              Option[Weather] = None,
    events :               Option[Array[Event]] = None,
    houseDetails :         Option[HouseDetails] = None,
    program :              Option[Program] = None,
    equipmentStatus :      Option[EquipmentStatusListItem] = None,
    notificationSettings : Option[NotificationSettings] = None,
    version :              Option[Version] = None,
    securitySettings :     Option[SecuritySettings] = None,
    remoteSensors :        Option[Array[RemoteSensor]] = None
  ) = Thermostat(
    identifier = identifier,
    name = name,
    thermostatRev = thermostatRev,
    isRegistered = isRegistered,
    modelNumber = modelNumber,
    brand = brand,
    features = features,
    lastModified = lastModified,
    thermostatTime = thermostatTime,
    utcTime = utcTime,
    audio = audio,
    alerts = alerts, 
    settings = settings, 
    runtime = runtime, 
    extendedRuntime = extendedRuntime, 
    electricity = electricity, 
    devices = devices, 
    location = location, 
    technician = technician, 
    utility = utility,
    management = management, 
    weather = weather, 
    events = events, 
    houseDetails = houseDetails, 
    program = program, 
    equipmentStatus = equipmentStatus, 
    notificationSettings = notificationSettings, 
    version = version, 
    securitySettings = securitySettings,
    remoteSensors = remoteSensors 
  )


  protected def thermRuntime(
    runtimeRev : String = "111111",
    connected : Boolean = true,
    firstConnected : Time.FullDate = new FullDate(),
    connectDateTime : Time.FullDate = new FullDate(),
    disconnectDateTime : Time.FullDate = new FullDate(),
    lastModified : Time.FullDate = new FullDate(),
    lastStatusModified : Time.FullDate = new FullDate(),
    runtimeDate : Time.DateOnly = new DateOnly(),
    runtimeInterval : Int = 0,
    actualTemperature : Int = 720,
    actualHumidity : Int = 60,
    rawTemperature : Int = 720,
    showIconMode : WeatherForecast.WeatherIcon = WeatherForecast.WeatherIcon.None,
    desiredHeat : Int = 680,
    desiredCool : Int = 740,
    desiredHumidity : Int = 35,
    desiredDehumidity : Int = 65,
    desiredFanMode : FanMode.Entry = FanMode.Auto,
    desiredHeatRange : Array[Int] = Array(520,800),
    desiredCoolRange : Array[Int] = Array(620,900)
  ) = ThermostatRuntime(
    runtimeRev = runtimeRev,
    connected = connected,
    firstConnected = firstConnected,
    connectDateTime = connectDateTime,
    disconnectDateTime= disconnectDateTime,
    lastModified= lastModified,
    lastStatusModified= lastStatusModified,
    runtimeDate= runtimeDate,
    runtimeInterval= runtimeInterval,
    actualTemperature= actualTemperature,
    actualHumidity= actualHumidity,
    rawTemperature= rawTemperature,
    showIconMode= showIconMode,
    desiredHeat= desiredHeat,
    desiredCool= desiredCool,
    desiredHumidity= desiredHumidity,
    desiredDehumidity= desiredDehumidity,
    desiredFanMode= desiredFanMode,
    desiredHeatRange= desiredHeatRange,
    desiredCoolRange= desiredCoolRange 
  )

}
