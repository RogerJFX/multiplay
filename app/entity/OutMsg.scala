package entity

import play.api.libs.json.{Json, OFormat}

// Same as InMsg. Should change eventually.
case class OutMsg(task: String, ts: Long, data: String)

object OutMsg {
  implicit def format: OFormat[OutMsg] = Json.format[OutMsg]
}