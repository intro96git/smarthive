package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Audio properties of the thermostat (Ecobee4 only)
  *
  * @param playbackVolume The volume level for audio playback. This includes volume of the voice assistant. A value between 0 and 100.
  * @param microphoneEnabled Turn microphone (privacy mode) on and off.
  * @param soundAlertVolume The volume level for alerts on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  * @param soundTickVolume The volume level for key presses on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  * @param voiceEngines The list of compatible voice engines
  */
case class Audio(
    playbackVolume :    Int,
    microphoneEnabled : Boolean,
    soundAlertVolume :  Int,
    soundTickVolume :   Int,
    voiceEngines :      Array[VoiceEngine]
)

object Audio {
  implicit val AudioFormat = DefaultJsonProtocol.jsonFormat5(Audio.apply)
}
