package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.test.BaseTestSpec

import spray.json._
import spray.json.DefaultJsonProtocol._

class ProgramScheduleSpec extends BaseTestSpec {


  "Program Schedule days" must "be initializable with defaults" in {
    val defaultClimate = Climate.Ref("default")
    val day = new ProgramSchedule.Day(defaultClimate)
    day.hours foreach { _ shouldBe defaultClimate }
    day.hours.size shouldBe 24

    val newC = Climate.Ref("new")
    val newDay = day.withHourUsingClimate(2, newC)
    newDay.hours.take(4) shouldBe Array(defaultClimate, defaultClimate, newC, defaultClimate)

    day.withHoursSetToClimate(1 to 3, newC).hours.take(5) shouldBe Array(defaultClimate, newC, newC, newC, defaultClimate)
    day.withClimates((1, newC), (4, newC)).hours.take(5) shouldBe Array(defaultClimate, newC, defaultClimate, defaultClimate, newC)
  }


  they must "not allow illegal hours specified" in {
    val day = new ProgramSchedule.Day(Climate.Ref("def"))
    day.withClimates((0, Climate.Ref("ok")),(23, Climate.Ref("ok")))
    intercept[IllegalArgumentException] { day.withClimates((-1, Climate.Ref("bad")),(0, Climate.Ref("bad"))) }
    intercept[IllegalArgumentException] { day.withClimates((23, Climate.Ref("bad")), (24, Climate.Ref("bad"))) }

    day.hours(0)
    day.hours(23)
    intercept[ArrayIndexOutOfBoundsException] { day.hours(24) }

    day.withHoursSetToClimate(0 to 23, Climate.Ref("ok"))
    intercept[IllegalArgumentException] { day.withHoursSetToClimate(-1 to 0, Climate.Ref("bad")) }

    day.withHourUsingClimate(0, Climate.Ref("ok"))
    day.withHourUsingClimate(23, Climate.Ref("ok"))
    intercept[IllegalArgumentException] { day.withHourUsingClimate(-1, Climate.Ref("bad")) }
    intercept[IllegalArgumentException] { day.withHourUsingClimate(24, Climate.Ref("bad")) }
  }



  "Program Schedules" must "be initalizable with defaults" in {
    val defaultRef = Climate.Ref("default")
    val schedule = ProgramSchedule.withDefaultClimate(defaultRef)

    schedule.sunday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.monday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.tuesday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.wednesday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.thursday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.friday.hours shouldBe Array.fill(24)(defaultRef)
    schedule.saturday.hours shouldBe Array.fill(24)(defaultRef)
  }

  they must "be serializable to JSON" in {
    val defSchedule = ProgramSchedule.withDefaultClimate(Climate.Ref("home"))
    val sched = defSchedule.copy(
        monday = defSchedule.monday.withHoursSetToClimate(9 to 17, Climate.Ref("away")),
        tuesday = defSchedule.tuesday.withHoursSetToClimate(9 to 17, Climate.Ref("away")),
        wednesday = defSchedule.wednesday.withHoursSetToClimate(9 to 17, Climate.Ref("away")),
        thursday = defSchedule.thursday.withHoursSetToClimate(9 to 17, Climate.Ref("away")),
        friday = defSchedule.friday.withHoursSetToClimate(9 to 17, Climate.Ref("away"))
    )

    val expected = s"""[
      ["home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","away","away","away","away","away","away","away","away","away","home","home","home","home","home","home"],
      ["home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home","home"]
    ]""".parseJson


    sched.toJson shouldBe expected
    sched.toJson.convertTo[ProgramSchedule] shouldBe sched
  }
}