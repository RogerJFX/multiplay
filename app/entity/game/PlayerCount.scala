package entity.game

import play.api.libs.json.{Json, OFormat}

case class PlayerCount(count: Int)

object PlayerCount {
  implicit def format: OFormat[PlayerCount] = Json.format[PlayerCount]
}