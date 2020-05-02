package entity.game

import Types.PlayerDef
import play.api.libs.json.{Json, OFormat}

case class PlayerDTO(player: PlayerDef)

object PlayerDTO {
  implicit def format: OFormat[PlayerDTO] = Json.format[PlayerDTO]
}