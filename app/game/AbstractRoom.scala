package game

import java.util.UUID

import entity.game.{ChatDTO, PlayerListDTO}
import entity.{OutMsg, Task}
import game.solitaire.Player
import util.SimpleJsonParser

import scala.collection.mutable

abstract class AbstractRoom (val name: String, val master: Player, val maxPlayers: Int) extends SimpleJsonParser with Task{
  var closed: Boolean = false

  val players: mutable.ListBuffer[Player] = mutable.ListBuffer[Player](master)

  val uuid: UUID = UUID.randomUUID()

  broadcastPlayers()

  def broadcastRooms()

  def runGame()

  def forwardGameData(uuid: UUID, data: String)

  def callPlayer(player: Player)

  def addPlayer(player: Player): Boolean = {
    if(!closed && players.size < maxPlayers && !players.contains(player)) {
      players.append(player)
      callPlayer(player)
      broadcastPlayers()
      broadcastRooms()
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
    broadcastRooms()
  }

  def kickPlayer(player: Player): Unit = {
    removePlayer(player)
    player.send(OutMsg(OUT_KICKED, 0, "{}"))
  }

  def closeAndStart(): Unit = {
    closed = true
    broadcastRooms()
    runGame()
    // TODO: start it
  }

  def killRoom(): Unit = {
    players.foreach(player => {
      player.send(OutMsg(OUT_ROOM_KILLED, 0, "{}"))
    })
  }

  def broadcastRawMessage(playerName: String, msg: String): Unit = {
    players.foreach(player => {
      player.send(OutMsg(TASK_CHAT, 0, t2JsonString[ChatDTO](ChatDTO(playerName, msg))))
    })
  }

  private def broadcastPlayers(): Unit = {
    players.foreach(player => {
      player.send(OutMsg(OUT_PLAYERS_IN_ROOM, 0, t2JsonString[PlayerListDTO](PlayerListDTO(players.map(p => (p.uuid, p.name)).toSeq))))
    })
  }
}
