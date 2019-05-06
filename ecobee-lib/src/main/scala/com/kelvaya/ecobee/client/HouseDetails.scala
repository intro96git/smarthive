package com.kelvaya.ecobee.client

import com.kelvaya.util.SprayImplicits
import com.kelvaya.util.enum.JsonIntEnum
import com.kelvaya.util.enum.JsonStringEnum

import spray.json.DefaultJsonProtocol


/** Details about a house where a [[Thermostat]] is located */
case class HouseDetails(
    style : HouseDetails.Style,
    size :  Int,
    numberOfFloors : Int,
    numberOfRooms : Int,
    numberOfOccupants : Int,
    age : Int,
    windowEfficiency : HouseDetails.WindowEfficiency
)



object HouseDetails extends SprayImplicits {

  type Style = Style.Entry
  object Style extends JsonStringEnum {
    val Other = Val("Other")
    val Apartment = Val("apartment")
    val Condominium = Val("condominium")
    val Detached = Val("detached")
    val Loft = Val("loft")
    val MultiPlex = Val("multiPlex")
    val RowHouse = Val("rowHouse")
    val SemiDetached = Val("semiDetached")
    val Townhouse = Val("townhouse")
    val Unknown = Val("0")
  }

  
  type WindowEfficiency = WindowEfficiency.Entry
  object WindowEfficiency extends JsonIntEnum {
    /** R value of 0.8 */
    val R0P8 = Val(1)

    /** R value of 0.96 */
    val R0P96 = Val(2)

    /** R value of 1.65 */
    val R1P65 = Val(3)

    /** R value of 2.01 */
    val R2P01 = Val(4)

    /** R value of 2.5 */
    val R2P5 = Val(5)

    /** R value of 1.14 */
    val R1P14 = Val(6)

    /** R value of 1.37 */
    val R1P37 = Val(7)
  }

  implicit val HouseDetailsFormat = DefaultJsonProtocol.jsonFormat7(HouseDetails.apply)
}
