package entity

import play.api.libs.json.{Json, OFormat}

case class InMsg(msg: String)

object InMsg {
  implicit def format: OFormat[InMsg] = Json.format[InMsg]
}
