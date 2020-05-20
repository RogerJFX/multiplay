package entity

trait Task {
  val TASK_ROOM_CREATE = "roomCreate"
  val TASK_ROOM_ENTER = "roomEnter"
  val TASK_ROOM_LIST = "roomList"
  val TASK_COME_IN = "howdy"
  val TASK_SIZES = "count"
  val TASK_PING = "ping"
  val TASK_VOID = "void"
  val TASK_ROOM_KICK = "roomKick"
  val TASK_ROOM_LEAVE = "roomLeave" // or kill, if master
  val TASK_START_GAME = "startGame"
  val TASK_GAME_INTERNAL = "game"

  val TASK_CHAT = "chat"
  val TASK_GAME = "game"

  val OUT_KICKED = "kicked"
  val OUT_ROOM_KILLED = "roomKilled"
  val OUT_PLAYERS_IN_ROOM = "playersInRoom"

  def now(): Long = System.currentTimeMillis()
}