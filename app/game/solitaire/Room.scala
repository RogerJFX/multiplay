package game.solitaire

import java.util.UUID

import entity.OutMsg
import entity.game.PlayerListDTO
import game.Player
import util.SimpleJsonParser

import scala.collection.mutable

class Room(val name: String, val master: Player, val maxPlayers: Int) extends SimpleJsonParser {

  var closed: Boolean = false

  val players: mutable.ListBuffer[Player] = mutable.ListBuffer[Player](master)

  val uuid: UUID = UUID.randomUUID()

  broadcastPlayers()

  def addPlayer(player: Player): Unit = {
    if(!closed) {
      players.append(player)
      broadcastPlayers()
    }
  }

  def removePlayer(player: Player): Unit = {
    if(player == master) {
      players.remove(players.indexOf(player))
      killRoom()
    } else if(player != master && !closed) {
      players.remove(players.indexOf(player))
      broadcastPlayers()
    }
  }

  def closeAndStart(): Unit = {
    closed = true
  }

  def killRoom(): Unit = {
    players.foreach(player => {
      player.send(OutMsg("roomKilled", 0, "{}"))
    })
  }

  private def broadcastPlayers() = {
    players.foreach(player => {
      player.send(OutMsg("playersInRoom", 0, t2JsonString[PlayerListDTO](PlayerListDTO(players.map(p => (p.uuid, p.name)).toSeq))))
    })
  }
}
