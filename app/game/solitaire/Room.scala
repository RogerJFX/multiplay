package game.solitaire

import java.util.UUID

import entity.Task
import game.AbstractRoom
import util.SimpleJsonParser

class Room(override val name: String, override val master: Player, override val maxPlayers: Int, lobby: Lobby)
  extends AbstractRoom(name, master, maxPlayers) with SimpleJsonParser with Task{

  val game = new Game(players)

  override def broadcastRooms(): Unit = lobby.broadcastRooms()

  override def callPlayer(player: Player): Unit = player.myRoom = Some(this)

  override def runGame(): Unit = game.run()

  override def forwardGameData(uuid: UUID, data: String): Unit = game.takeGameData(uuid, data)
}
