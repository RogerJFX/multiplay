package entity.game

import java.util.UUID

import play.api.libs.json.{Json, OFormat}

case class UuidDTO(uuid: UUID)

object UuidDTO {
  implicit def format: OFormat[UuidDTO] = Json.format[UuidDTO]
}