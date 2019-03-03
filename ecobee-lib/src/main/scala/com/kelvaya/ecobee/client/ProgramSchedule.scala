package com.kelvaya.ecobee.client

import scala.collection.mutable.ListBuffer
import spray.json._
import spray.json.DefaultJsonProtocol._



/** [[Thermostat]] schedule
  *
  * Each [[ProgramSchedule#Day day]] specifies the [[Climate.Ref references]] to [[Climate]] to use for each hour of that day.
  *
  * @param sunday The climate refs to use for each hour of Sunday
  * @param monday The climate refs to use for each hour of Monday
  * @param tuesday The climate refs to use for each hour of Tuesday
  * @param wednesday The climate refs to use for each hour of Wednesday
  * @param thursday The climate refs to use for each hour of Thursday
  * @param friday The climate refs to use for each hour of Friday
  * @param saturday The climate refs to use for each hour of Saturday
  */
case class ProgramSchedule(
    sunday :    ProgramSchedule.Day,
    monday :    ProgramSchedule.Day,
    tuesday :   ProgramSchedule.Day,
    wednesday : ProgramSchedule.Day,
    thursday :  ProgramSchedule.Day,
    friday :    ProgramSchedule.Day,
    saturday :  ProgramSchedule.Day
)



object ProgramSchedule {

  /** Returns a new [[ProgramSchedule]] with every hour of every day set to use the given climate */
  def withDefaultClimate(ref : Climate.Ref) = {
    val defaultDay = new Day(ref)
    ProgramSchedule(defaultDay, defaultDay, defaultDay, defaultDay, defaultDay, defaultDay, defaultDay)
  }


  /** A [[ProgramSchedule]] day.
    *
    * A day contains each hour's climate setting.
    *
    * @note To create a new day, you must use the secondary constructor, passing in the default [[Climate.Ref]] for all
    * 24 hours.  You then may use a "with" method to modify one or more hours of the day.
    *
    * @param hours A sequence of 24 climate references
    */
  case class Day private (hours : Seq[Climate.Ref]) {
    def this(defaultClimate : Climate.Ref) = this(Array.fill(24)(defaultClimate))

    /** Return a new day with the same climate settings as the current with the exception of the given hour.
      *
      *  @param hour The hour to modify
      *  @param ref The climate that will be used on the given hour
      */
    def withHourUsingClimate(hour : Int, ref : Climate.Ref) = {
      if (hour >=0 && hour <=23) Day(hours.updated(hour, ref))
      else throw new IllegalArgumentException("`hour` must be between 0 and 23 (inclusive)")
    }


    /** Return a new day with the same climate settings as the current with the exception of the given hours.
      *
      *  @param climate A tuple of (hour,climate) that will override the current day's settings
      */
    def withClimates(climate : (Int,Climate.Ref)*) = {
      val list = hours.toBuffer
      climate foreach { case (h,c) =>
        if (h < 0 || h > 23) throw new IllegalArgumentException("`hour` must be between 0 and 23 (inclusive)")
        list.update(h, c)
      }
      Day(list.toSeq)
    }


    /** Return a new day with the same climate settings as the current with the exception of the given hours.
      *
      *  @param hourRange The hours to modify
      *  @param ref The climate that will be used during the given hours
      */
    def withHoursSetToClimate(hourRange : Range, climate : Climate.Ref) = {
      val list = hours.toBuffer
      hourRange foreach { h =>
        if (h < 0 || h > 23) throw new IllegalArgumentException("`hour` must be between 0 and 23 (inclusive)")
        list.update(h, climate)
      }
      Day(list.toSeq)
    }
  }



  object Day {
    /** Serializes a [[Day]] into JSON */
    implicit object DayFormat extends JsonFormat[Day] {
      def read(json: JsValue): Day = json match {
        case a : JsArray => {
          val elems = a.elements
          if (elems.size != 24) deserializationError(s"${json} is not a valid Program Schedule Day")
          val hours = elems map { e =>
            e match {
              case s : JsString => Climate.Ref(s.value)
              case _ => deserializationError(s"${json} is not a valid Program Schedule Day; bad element ${e}")
            }
          }
          new Day(hours)
        }

        case _ => deserializationError(s"${json} is not a valid Program Schedule Day")
      }

      def write(obj: Day): JsValue = JsArray(obj.hours.map(_.name.toJson).toList)
    }
  }


  /** Serializes a [[ProgramSchedule]] into JSON */
  implicit object ProgramScheduleFormat extends JsonFormat[ProgramSchedule] {
    def read(json: JsValue): ProgramSchedule = json match {
      case a : JsArray => {
        val elems = a.elements
        if (elems.size != 7) deserializationError(s"${json} is not a valid Program Schedule")
        val days = elems map { Day.DayFormat.read }
        ProgramSchedule(days(0), days(1), days(2), days(3), days(4), days(5), days(6))
      }
      case _ => deserializationError(s"${json} is not a valid Program Schedule")
    }

    def write(obj: ProgramSchedule): JsValue = JsArray(
      obj.sunday.toJson,
      obj.monday.toJson,
      obj.tuesday.toJson,
      obj.wednesday.toJson,
      obj.thursday.toJson,
      obj.friday.toJson,
      obj.saturday.toJson
    )
  }
}