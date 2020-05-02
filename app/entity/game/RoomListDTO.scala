package entity.game

import entity.game.Types.RoomDef
import play.api.libs.json.{Json, OFormat}

case class RoomListDTO(rooms: Seq[RoomDef])

object RoomListDTO {
  implicit def format: OFormat[RoomListDTO] = Json.format[RoomListDTO]
}
