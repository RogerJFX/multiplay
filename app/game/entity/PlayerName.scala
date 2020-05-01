package game.entity

import play.api.libs.json.{Json, OFormat}

case class PlayerName(name: String)

object PlayerName {
  implicit def format: OFormat[PlayerName] = Json.format[PlayerName]
}