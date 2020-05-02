package game.solitaire

import java.util.UUID

import entity.OutMsg
import entity.game.RoomListDTO
import game.Player
import util.SimpleJsonParser

import scala.collection.concurrent.TrieMap

object Lobby extends SimpleJsonParser {

  private val playerMap = new TrieMap[UUID, Player]()
  private val roomMap = new TrieMap[UUID, Room]()

  def addPlayer(uuid: UUID, player: Player): Option[Player] = {
    val opt = playerMap.put(uuid, player)
    broadcastCount()
    opt
  }

  def removePlayer(uuid: UUID): Option[Player] = {
    val opt = playerMap.remove(uuid)
    findRoomOfMasterPlayer(opt)
    broadcastCount()
    opt
  }

  def addRoom(room: Room): Option[Room] = {
    val opt: Option[Room] = roomMap.put(room.uuid, room)
    broadcastRooms()
    broadcastCount()
    opt
  }

  def removeRoom(uuid: UUID): Option[Room] = {
    val opt = roomMap.remove(uuid)
    broadcastRooms()
    broadcastCount()
    opt
  }

  def getRoom(uuid: UUID): Option[Room] = roomMap.get(uuid)

  def getPlayer(uuid: UUID): Option[Player] = playerMap.get(uuid);

  def getIdlePlayers: Seq[Player] = {
    playerMap.values.filter(p => !p.busy).toSeq
  }

  def getOpenRooms: Seq[Room] = {
    roomMap.values.filter(r => !r.closed).toSeq
  }

  def findRoomOfMasterPlayer(playerOpt: Option[Player]) = {
    if(playerOpt.isDefined) {
      val roomOpt = roomMap.values.find(r => r.master == playerOpt.get)
      if(roomOpt.isDefined) {
        val room = roomOpt.get
        room.killRoom()
        removeRoom(room.uuid)
      }
    }
  }

  def createRoomsMsg(ts: Long = 0L): OutMsg = {
    OutMsg("rooms", ts, t2JsonString[RoomListDTO](RoomListDTO(roomMap.values.map(room => {
      (room.uuid, room.name, room.maxPlayers, room.players.size)
    }).toSeq)))
  }

  private def broadcastRooms(): Unit = {
    val msg = createRoomsMsg()
    getIdlePlayers.foreach(p => {
      p.send(msg)
    })
  }

  private def broadcastCount() = {
    val c = playerMap.size
    val r = roomMap.size
    val msg = OutMsg("count", 0, s"""{"players": $c, "rooms": $r}""")
    playerMap.values.foreach(p => p.send(msg))
  }
}
