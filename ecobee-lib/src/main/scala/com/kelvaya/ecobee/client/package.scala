package com.kelvaya.ecobee

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import spray.json._
import akka.http.scaladsl.model.HttpResponse
import com.kelvaya.util.enum.JsonEnum

package object client {

  JsonEnum.registerEnums(AlertType,AlertAck,AlertNotificationType,AlertSeverity,AlertNumber,Climate.Owner, Climate.Type,
      DehumidifierMode, Device.OutputType, DRAccept, ExtendedRuntime.RuntimeHVACMode, Event.EventType, Event.FanMode, HoldAction, HouseDetails.Style,
      HouseDetails.WindowEfficiency, HumidifierMode, HVACMode, NotificationSettings.EquipmentType, NotificationSettings.GeneralType,
      NotificationSettings.LimitType, RemoteSensor.Type, Sensor.SensorType, Sensor.SensorUsage, Sensor.StateType, Sensor.ActionType,
      SensorCapability.Type, VentilatorMode, VentilatorType, WeatherForecast.WeatherIcon, WeatherForecast.CloudCover)

  // #######################

  type ServiceResponse[T] = Either[HttpResponse,T]

  /** Type used for API calls that have no [[ApiObject parameters]]. */
  type ParameterlessApi = EmptyApiObject.type

  /** Instance of the parameterless API object */
  lazy val EmptyApiObject = new WriteableApiObject { }

  /** JSON serializer for the parameterless API object */
  implicit val EmptyApiObjectFormat : RootJsonFormat[ParameterlessApi] = DefaultJsonProtocol.jsonFormat0 { () =>
    throw new NoSuchElementException("ParameterlessApi objects cannot be rendered to JSON")
  }

  type PinScope = PinScope.Value
  object PinScope extends Enumeration {
    val SmartWrite = Value("smartWrite")
    val SmartRead = Value("smartRead")
  }
  implicit val PinScopeFormatter : RootJsonFormat[PinScope] = new RootJsonFormat[PinScope] {
    // Members declared in spray.json.JsonReader
    def read(json: JsValue): PinScope = json match {
      case JsString(s) => PinScope.withName(s)
      case _ => throw new spray.json.DeserializationException("$json is not a recognized PinScope")
    }

    // Members declared in spray.json.JsonWriter
    def write(obj: PinScope): JsValue = JsString(obj.toString)
  }

  type TokenType = TokenType.Value
  object TokenType extends Enumeration {
    val Bearer = Value("Bearer")
  }
  implicit val TokenTypeFormatter : RootJsonFormat[TokenType] = new RootJsonFormat[TokenType] {
    // Members declared in spray.json.JsonReader
    def read(json: JsValue): TokenType = json match {
      case JsString(s) => TokenType.withName(s)
      case _ => throw new spray.json.DeserializationException("$json is not a recognized TokenType")
    }

    // Members declared in spray.json.JsonWriter
    def write(obj: TokenType): JsValue = JsString(obj.toString)
  }
}