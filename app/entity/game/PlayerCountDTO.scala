package entity.game

import play.api.libs.json.{Json, OFormat}

case class PlayerCountDTO(count: Int)

object PlayerCountDTO {
  implicit def format: OFormat[PlayerCountDTO] = Json.format[PlayerCountDTO]
}