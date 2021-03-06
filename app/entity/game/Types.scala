package entity.game

import java.util.UUID

object Types {

  /**
   * UUID, name, waiting
   */
  type PlayerDef = (UUID, String, Boolean)

  /**
   * UUID, room name, max players in room, current players, open
   */
  type RoomDef = (UUID, String, Int, Int, Boolean)

}
