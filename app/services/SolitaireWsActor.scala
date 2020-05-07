package services

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill}
import entity.game.{SimpleStringDTO, UuidDTO}
import entity.{InMsg, OutMsg, Task}
import game.AbstractPlayer
import game.solitaire.{Lobby, Player}
import util.SimpleJsonParser

class SolitaireWsActor(out: ActorRef, lobby: Lobby) extends Actor with SimpleJsonParser with Task {
  private val uuid = UUID.randomUUID()

  private def getPlayer(uuid: UUID = uuid): Player = {
    lobby.getPlayer(uuid).getOrElse(throw new RuntimeException("Cannot get player"))
  }

  private def welcome(data: String): String = {
    val playerName = jsonString2T[SimpleStringDTO](data)
    lobby.addPlayer(uuid, new Player(uuid, playerName.str, out, lobby))
    t2JsonString[UuidDTO](UuidDTO(uuid))
    // t2JsonString[PlayerListDTO](PlayerListDTO(Lobby.getIdlePlayers.map(p => (p.uuid, p.name))))
  }

  private def createRoom(data: String) = {
    val roomName = jsonString2T[SimpleStringDTO](data)
    val player = getPlayer()
    val room = player.createRoom(roomName.str)
    room match {
      case Some(room) =>
        t2JsonString[entity.game.PlayerDTO](entity.game.PlayerDTO(room.uuid, room.name))
      case _ =>
        """{"foul": -1}"""
    }
  }

  private def enterRoom(data:String) = {
    val _uuid = jsonString2T[UuidDTO](data).uuid
    val room = lobby.getRoom(_uuid)
    room match {
      case Some(room) =>
        if(room.addPlayer(getPlayer())) {
          t2JsonString[entity.game.PlayerDTO](entity.game.PlayerDTO(room.uuid, room.name))
        } else {
          """{"sorry": -1}"""
        }
      case _ =>
        """{"foul": -1}"""
    }
  }
  private def startGame() = {
    val myPlayer = getPlayer()
    val roomOpt = myPlayer.myRoom
    if(roomOpt.isDefined) {
      val room = lobby.getRoom(roomOpt.get.uuid)
      room match {
        case Some(room) =>
          if(room.master == myPlayer) {
            room.closeAndStart()
            """{"done": 0}"""
          } else {
            """{"sorry": -1}"""
          }
        case _ =>
          """{"foul": -1}"""
      }
    } else {
      """{"foul": -1}"""
    }

  }
  private def kickPlayer(data:String) = {
    val _uuid = jsonString2T[UuidDTO](data).uuid
    val myPlayer = getPlayer()
    val roomOpt = myPlayer.myRoom
    if(roomOpt.isDefined) {
      val room = lobby.getRoom(roomOpt.get.uuid)
      room match {
        case Some(room) =>
          if (room.master == myPlayer) {
            room.kickPlayer(getPlayer(_uuid))
            """{"done": 0}"""
          } else {
            """{"sorry": -1}"""
          }
        case _ =>
          """{"foul": -1}"""
      }
    } else {
      """{"foul": -1}"""
    }
  }

  private def chat(data: String): Unit = {
    val msg = jsonString2T[SimpleStringDTO](data).str
    val myPlayer = getPlayer()
    val roomOpt = myPlayer.myRoom
    if(roomOpt.isDefined) {
      val playerName = myPlayer.name
      val room = lobby.getRoom(roomOpt.get.uuid)
      if (room.isDefined) {
        room.get.broadcastRawMessage(playerName, msg)
      }
    }
  }

  private def resolve(msg: InMsg): Unit = {
    msg.task match {
      case TASK_PING =>
        out ! OutMsg(msg.task, msg.ts, """{"res": "pong"}""")
        case TASK_CHAT =>
          chat(msg.data)
      case TASK_COME_IN =>
        out ! OutMsg(msg.task, msg.ts, welcome(msg.data))
      case TASK_ROOM_LIST =>
        out ! lobby.createRoomsMsg(msg.ts)
      case TASK_ROOM_CREATE =>
        out ! OutMsg(TASK_ROOM_ENTER, msg.ts, createRoom(msg.data))
      case TASK_ROOM_ENTER =>
        out ! OutMsg(TASK_ROOM_ENTER, msg.ts, enterRoom(msg.data))
      case TASK_ROOM_KICK =>
        out ! OutMsg(TASK_VOID, msg.ts, kickPlayer(msg.data))
      case TASK_ROOM_LEAVE =>
        leaveRoom()
        lobby.closeRoomIfMasterPlayer(lobby.getPlayer(uuid))
        out ! OutMsg(OUT_KICKED, msg.ts, """{"bye": 0}""")
      case TASK_START_GAME =>
        out ! OutMsg(TASK_VOID, msg.ts, startGame())
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

  private def leaveRoom(): Unit = {
    val player = getPlayer()
    val room = player.myRoom
    if(room.isDefined) {
      room.get.removePlayer(player)
    }
  }

  private def goodbye() = {
    leaveRoom()
    lobby.removePlayer(uuid)
  }

  def killSocket(): Unit = {
    goodbye()
    self ! PoisonPill
  }
}
