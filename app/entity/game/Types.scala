package entity.game

import java.util.UUID

object Types {

  /**
   * UUID, name
   */
  type PlayerDef = (UUID, String)

  /**
   * UUID, room name, max players in room, current players
   */
  type RoomDef = (UUID, String, Int, Int)

}
