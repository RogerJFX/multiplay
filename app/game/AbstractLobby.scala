package game

import java.util.UUID

import entity.{OutMsg, Task}
import entity.game.RoomListDTO

import util.SimpleJsonParser

import scala.collection.concurrent.TrieMap

abstract class AbstractLobby extends SimpleJsonParser with Task {

  protected val playerMap = new TrieMap[UUID, AbstractPlayer]()
  private val roomMap = new TrieMap[UUID, AbstractRoom]()

  def addPlayer(uuid: UUID, player: AbstractPlayer): Option[AbstractPlayer] = {
    val opt = playerMap.put(uuid, player)
    broadcastCount()
    opt
  }

  def removePlayer(uuid: UUID): Option[AbstractPlayer] = {
    val opt = playerMap.remove(uuid)
    closeRoomIfMasterPlayer(opt)
    broadcastCount()
    opt
  }

  def addRoom(room: AbstractRoom): Option[AbstractRoom] = {
    val opt: Option[AbstractRoom] = roomMap.put(room.uuid, room)
    broadcastRooms()
    broadcastCount()
    opt
  }

  def removeRoom(uuid: UUID): Option[AbstractRoom] = {
    val opt = roomMap.remove(uuid)
    broadcastRooms()
    broadcastCount()
    opt
  }

  def getRoom(uuid: UUID): Option[AbstractRoom] = roomMap.get(uuid)

  def getPlayer(uuid: UUID): Option[AbstractPlayer] // = playerMap.get(uuid)

  def getIdlePlayers: Seq[AbstractPlayer] = {
    playerMap.values.filter(p => !p.busy).toSeq
  }

  def getOpenRooms: Seq[AbstractRoom] = {
    roomMap.values.filter(r => !r.closed).toSeq
  }

  def closeRoomIfMasterPlayer(playerOpt: Option[AbstractPlayer]): Unit = {
    if(playerOpt.isDefined) {
      val roomOpt = roomMap.values.find(r => r.master == playerOpt.get)
      if(roomOpt.isDefined) {
        val room = roomOpt.get
        room.killRoom()
        removeRoom(room.uuid)
      }
    }
  }

  def createRoomsMsg(ts: Long = 0L): OutMsg =
    OutMsg(TASK_ROOM_LIST, ts, t2JsonString[RoomListDTO](RoomListDTO(roomMap.values.map(room => {
      (room.uuid, room.name, room.maxPlayers, room.players.size, !room.closed)
    }).toSeq)))

  def broadcastRooms(): Unit = {
    val msg = createRoomsMsg()
    getIdlePlayers.foreach(p => {
      p.send(msg)
    })
  }

  protected def broadcastCount(): Unit = {
    val c = playerMap.size
    val r = roomMap.size
    val msg = OutMsg(TASK_SIZES, 0, s"""{"players": $c, "rooms": $r}""")
    playerMap.values.foreach(p => p.send(msg))
  }
}
