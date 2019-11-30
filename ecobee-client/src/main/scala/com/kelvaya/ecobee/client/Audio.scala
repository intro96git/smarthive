package com.kelvaya.ecobee.client

import spray.json._
import spray.json.DefaultJsonProtocol._

/** Audio properties of the thermostat (Ecobee4 only)
  *
  * @note This can be used in GET requests only.  Use the [[#asWriteable]] method to grab an instance valid for writing in POST operations.
  *
  * @param playbackVolume The volume level for audio playback. This includes volume of the voice assistant. A value between 0 and 100.
  * @param microphoneEnabled Turn microphone (privacy mode) on and off.
  * @param soundAlertVolume The volume level for alerts on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  * @param soundTickVolume The volume level for key presses on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  * @param voiceEngines The list of compatible voice engines
  */
case class Audio(
    playbackVolume :    Option[Int],
    microphoneEnabled : Option[Boolean],
    soundAlertVolume :  Option[Int],
    soundTickVolume :   Option[Int],
    voiceEngines :      Option[Array[VoiceEngine]]
) extends ApiObject {
  def asWriteable = AudioModification(playbackVolume, microphoneEnabled, soundAlertVolume, soundTickVolume)
}

object Audio {
  implicit val AudioFormat = DefaultJsonProtocol.jsonFormat5(Audio.apply)
}



/** Audio properties of the thermostat (Ecobee4 only) which can be used in POST modification requests.
  *
  * @param playbackVolume The volume level for audio playback. This includes volume of the voice assistant. A value between 0 and 100.
  * @param microphoneEnabled Turn microphone (privacy mode) on and off.
  * @param soundAlertVolume The volume level for alerts on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  * @param soundTickVolume The volume level for key presses on the thermostat. A value between 0 and 10, with 0 meaning 'off'
  *
  * @see [[Audio]]
  */
case class AudioModification(
    playbackVolume :    Option[Int] = None,
    microphoneEnabled : Option[Boolean] = None,
    soundAlertVolume :  Option[Int] = None,
    soundTickVolume :   Option[Int] = None
) extends WriteableApiObject

object AudioModification {
  implicit val AudioModificationFormat = DefaultJsonProtocol.jsonFormat4(AudioModification.apply)
}
