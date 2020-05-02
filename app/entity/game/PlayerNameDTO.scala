package entity.game

import play.api.libs.json.{Json, OFormat}

case class PlayerNameDTO(name: String)

object PlayerNameDTO {
  implicit def format: OFormat[PlayerNameDTO] = Json.format[PlayerNameDTO]
}