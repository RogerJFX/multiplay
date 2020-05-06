package entity.game

import play.api.libs.json.{Json, OFormat}

case class ChatDTO(name: String, msg: String)

object ChatDTO {
  implicit def format: OFormat[ChatDTO] = Json.format[ChatDTO]
}