package com.kelvaya.ecobee

import com.kelvaya.util.jsonenum.JsonEnum

import spray.json.RootJsonFormat
import spray.json._


/** API client to access the Ecobee thermostat's web services.
  *
  =Setup=
  * Many of the classes participating in these API's use ZIO to track IO operations.
  *
  * To start using the API client, you must declare implicitly a number of options:
  - The [[com.kelvaya.ecobee.config.Settings Settings]] instance that reads global application settings.
  - The [[RequestExecutor]] instance, which controls the actual execution of HTTP requests against the Ecobee API
  - The `ActorSystem` instance, used by Akka to coordinate all actor interactions
  - The Akka `LoggingBus` instance, used for logging.
  *
  * This can be done through the [[com.kelvaya.ecobee.config.DI DI]] class for ease-of-use.  It has sane defaults,
  * and allows overriding of the [[com.kelvaya.ecobee.config.Settings Settings]] and [[RequestExecutor]] for individual situations through the
  * [[com.kelvaya.ecobee.config.DI.Dependencies Dependencies]] case class.
  *
  * Please note that there is no default set for the `ActorSystem`. You will always need to pass that in.
  *
  * Typical setup:
{{{
final class MyApp extends Application {

  // Initialize dependencies using all defaults
  val deps = DI(ActorSystem("my-actor-sys"))
  import deps.Implicits._
}
}}}
  *
  = Working with the Client =
  * DOCS INCOMPLETE ... need more info!
  */
package object client extends ClientEnvDefinition {

  /*
   * Workaround for an issue loading available JSON-serializable enumerations.  These enumerations must be loaded
   * into memory before any deserialization occurs, as the system compares JSON payloads to those already loaded
   * during the deserialzation process.  This method forcibly preloads all enumeration classes that should be
   * recognized.
   */
  JsonEnum.preloadEnums(AlertType,AlertAck,AlertNotificationType,AlertSeverity,AlertNumber,Climate.Owner, Climate.Type,
      DehumidifierMode, Device.OutputType, DRAccept, ExtendedRuntime.RuntimeHVACMode, Event.EventType, Event.FanMode, HoldAction, HouseDetails.Style,
      HouseDetails.WindowEfficiency, HumidifierMode, HVACMode, NotificationSettings.EquipmentType, NotificationSettings.GeneralType,
      NotificationSettings.LimitType, RemoteSensor.Type, Sensor.SensorType, Sensor.SensorUsage, Sensor.StateType, Sensor.ActionType,
      SensorCapability.Type, VentilatorMode, VentilatorType, WeatherForecast.WeatherIcon, WeatherForecast.CloudCover)

  // #######################

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
      case _ => throw new spray.json.DeserializationException(s"$json is not a recognized PinScope")
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
      case _ => throw new spray.json.DeserializationException(s"$json is not a recognized TokenType")
    }

    // Members declared in spray.json.JsonWriter
    def write(obj: TokenType): JsValue = JsString(obj.toString)
  }
}
