package game.solitaire

import java.util.UUID

import entity.{OutMsg, Task}
import entity.game.PlayerListDTO
import game.Player
import util.SimpleJsonParser

import scala.collection.mutable

class Room(val name: String, val master: Player, val maxPlayers: Int, lobby: Lobby) extends SimpleJsonParser with Task{

  var closed: Boolean = false

  val players: mutable.ListBuffer[Player] = mutable.ListBuffer[Player](master)

  val uuid: UUID = UUID.randomUUID()

  broadcastPlayers()

  def addPlayer(player: Player): Boolean = {
    if(!closed && players.size < maxPlayers && !players.contains(player)) {
      players.append(player)
      broadcastPlayers()
      lobby.broadcastRooms()
      player.myRoom = this
      true
    } else {
      false
    }
  }

  def removePlayer(player: Player): Unit = {
    player.leaveRoom()
    if(player == master) {
      players.remove(players.indexOf(player))
      killRoom()
    } else if(player != master /*&& !closed*/) {
      players.remove(players.indexOf(player))
      broadcastPlayers()
    }
    lobby.broadcastRooms()
  }

  def kickPlayer(player: Player): Unit = {
    removePlayer(player);
    player.send(OutMsg(OUT_KICKED, 0, "{}"))
  }

  def closeAndStart(): Unit = {
    closed = true
    lobby.broadcastRooms()
    // TODO: start it
  }

  def killRoom(): Unit = {
    players.foreach(player => {
      player.send(OutMsg(OUT_ROOM_KILLED, 0, "{}"))
    })
  }

  private def broadcastPlayers() = {
    players.foreach(player => {
      player.send(OutMsg(OUT_PLAYERS_IN_ROOM, 0, t2JsonString[PlayerListDTO](PlayerListDTO(players.map(p => (p.uuid, p.name)).toSeq))))
    })
  }
}
