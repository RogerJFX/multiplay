package services

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill}
import entity.game.{PlayerListDTO, PlayerNameDTO, UuidDTO}
import entity.{InMsg, OutMsg, Task}
import game.Player
import game.solitaire.Lobby
import util.SimpleJsonParser

class SolitaireWsActor(out: ActorRef) extends Actor with SimpleJsonParser with Task {
  private val uuid = UUID.randomUUID()

  private def getPlayer() = {
    Lobby.getPlayer(uuid).getOrElse(throw new RuntimeException("Cannot get player"))
  }

  private def welcome(data: String): String = {
    val playerName = jsonString2T[PlayerNameDTO](data)
    Lobby.addPlayer(uuid, new Player(uuid, playerName.name, out))// .getOrElse(throw new RuntimeException("Cannot add player"))
    t2JsonString[PlayerListDTO](PlayerListDTO(Lobby.getIdlePlayers.map(p => (p.uuid, p.name))))
  }

  private def createRoom(data: String) = {
    val roomName = jsonString2T[PlayerNameDTO](data)
    val player = getPlayer()
    val room = player.createRoom(roomName.name)
    room match {
      case Some(room) =>
        t2JsonString[entity.game.PlayerDTO](entity.game.PlayerDTO(room.uuid, room.name))
      case _ =>
        """{"foul": -1}"""
    }
  }
  // UuidDTO
  private def enterRoom(data:String) = {
    val _uuid = jsonString2T[UuidDTO](data).uuid
    val room = Lobby.getRoom(_uuid)
    room match {
      case Some(room) =>
        room.addPlayer(getPlayer())
    }
  }

  private def resolve(msg: InMsg): Unit = {
    msg.task match {
      case TASK_PING =>
        out ! OutMsg(msg.task, msg.ts, """{"res": "pong"}""")
      case TASK_COME_IN =>
        out ! OutMsg(msg.task, msg.ts, welcome(msg.data))
      case TASK_ROOM_LIST =>
        out ! Lobby.createRoomsMsg(msg.ts)
      case TASK_ROOM_CREATE =>
        out ! OutMsg(TASK_ROOM_ENTER, msg.ts, createRoom(msg.data))
      case TASK_ROOM_ENTER =>
        out ! OutMsg(TASK_ROOM_ENTER, msg.ts, createRoom(msg.data))
    }
  }

  override def receive: Receive = {
    case msg: InMsg =>
      resolve(msg)
    case _ =>
      throw new RuntimeException("Not acceptable")
  }

  override def postStop(): Unit = {
    goodbye()
    // println("closed")
  }

  override def preStart(): Unit = {
    // println("connect")
  }

  private def goodbye() = {
    val player = getPlayer()
    val room = player.myRoom
    if(room != null) {
      room.removePlayer(player);
    }
    Lobby.removePlayer(uuid)
  }

  def killSocket(): Unit = {
    goodbye()
    self ! PoisonPill
  }
}
