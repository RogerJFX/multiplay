package entity.game

import play.api.libs.json.{Json, OFormat}

case class SimpleStringDTO(str: String)

object SimpleStringDTO {
  implicit def format: OFormat[SimpleStringDTO] = Json.format[SimpleStringDTO]
}