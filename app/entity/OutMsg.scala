package entity

import play.api.libs.json.{Json, OFormat}

case class OutMsg(task: String, ts: Long, data: String)

object OutMsg {
  implicit def format: OFormat[OutMsg] = Json.format[OutMsg]
}