package entity.game

import Types.PlayerDef
import play.api.libs.json.{Json, OFormat}

case class PlayerListDTO(players: Seq[PlayerDef])

object PlayerListDTO {
  implicit def format: OFormat[PlayerListDTO] = Json.format[PlayerListDTO]
}
