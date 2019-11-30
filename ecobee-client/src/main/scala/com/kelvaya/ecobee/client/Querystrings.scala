package com.kelvaya.ecobee.client

import com.kelvaya.ecobee.config.Settings

object Querystrings {
  type Entry = Tuple2[String, String]

  val JsonFormat: Entry = (("format", "json"))

  object ResponseType {
    val EcobeePIN: Entry = (("response_type", "ecobeePin"))
  }

  object Scope {
    val SmartWrite: Entry = (("scope", PinScope.SmartWrite.toString()))
    val SmartRead: Entry = (("scope", PinScope.SmartRead.toString()))
  }

  object GrantType {
    val Pin : Entry = (("grant_type", "ecobeePin"))
    val RefreshToken : Entry = (("grant_type", "refresh_token"))
  }

  private var _clientId: Option[Entry] = None
  def ClientId(implicit settings: Settings) = _clientId.getOrElse {
    _clientId = Some(("client_id", settings.EcobeeAppKey))
    _clientId.get
  }
}