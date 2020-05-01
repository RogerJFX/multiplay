package game.entity

import game.entity.PlayerList.playerUuidAndName
import play.api.libs.json.{Json, OFormat}

case class PlayerList(players: Seq[playerUuidAndName])

object PlayerList {
  type playerUuidAndName = (String, String)
  implicit def format: OFormat[PlayerList] = Json.format[PlayerList]
}
