package game

import java.util.UUID

class Player(val uuid: UUID, val name: String) {
  var busy: Boolean = false
}
