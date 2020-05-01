package entity

import play.api.libs.json.{Json, OFormat}

case class OutMsg(msg: String)

object OutMsg {
  implicit def format: OFormat[OutMsg] = Json.format[OutMsg]
}