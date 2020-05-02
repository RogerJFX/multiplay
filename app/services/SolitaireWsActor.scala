package services

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill}
import entity.game.{PlayerListDTO, PlayerNameDTO}
import entity.{InMsg, OutMsg}
import game.Player
import game.solitaire.Lobby
import util.SimpleJsonParser

class SolitaireWsActor(out: ActorRef) extends Actor with SimpleJsonParser {
  private val uuid = UUID.randomUUID()

  private def welcome(data: String): String = {
    val playerName = jsonString2T[PlayerNameDTO](data)
    Lobby.addPlayer(uuid, new Player(uuid, playerName.name, out))// .getOrElse(throw new RuntimeException("Cannot add player"))
    t2JsonString[PlayerListDTO](PlayerListDTO(Lobby.getIdlePlayers.map(p => (p.uuid, p.name))))
  }

  private def createRoom(data: String) = {
    val roomName = jsonString2T[PlayerNameDTO](data)
    val player = Lobby.getPlayer(uuid).getOrElse(throw new RuntimeException("Cannot get player"))
    val room = player.createRoom(roomName.name)
    room match {
      case Some(room) =>
        t2JsonString[entity.game.PlayerDTO](entity.game.PlayerDTO(room.uuid, room.name))
      case _ =>
        """{"foul": -1}"""
    }

  }

  private def resolve(msg: InMsg): Unit = {
    msg.task match {
      case "ping" =>
        out ! OutMsg(msg.task, msg.ts, """{"res": "pong"}""")
      case "howdy" =>
        out ! OutMsg(msg.task, msg.ts, welcome(msg.data))
      case "rooms" =>
        out ! Lobby.createRoomsMsg(msg.ts)
      case "roomCreate" =>
        out ! OutMsg("roomEnter", msg.ts, createRoom(msg.data))
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
    val player = Lobby.getPlayer(uuid).getOrElse(throw new RuntimeException("Cannot get player"))
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
