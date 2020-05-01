package services

import java.util.UUID

import akka.actor.{Actor, ActorRef, PoisonPill}
import entity.game.{PlayerList, PlayerName}
import entity.{InMsg, OutMsg}
import game.Player
import game.solitaire.Lobby
import util.SimpleJsonParser

class SolitaireWsActor(out: ActorRef) extends Actor with SimpleJsonParser {
  private val uuid = UUID.randomUUID()

  private def welcome(data: String): String = {
    val playerName = jsonString2T[PlayerName](data)
    Lobby.addPlayer(uuid, new Player(uuid, playerName.name, out))
    t2JsonString[PlayerList](PlayerList(Lobby.getIdlePlayers.map(p => (p.uuid.toString, p.name))))
  }

  private def resolve(msg: InMsg): Unit = {
    msg.task match {
      case "ping" =>
        out ! OutMsg(msg.task, msg.ts, """{res: "pong"}""")
      case "howdy" =>
        out ! OutMsg(msg.task, msg.ts, welcome(msg.data))
    }
  }
  override def receive: Receive = {
    case msg: InMsg =>
      resolve(msg)
    case _ =>
      throw new RuntimeException("Not acceptable")
  }

  override def postStop(): Unit = {
    Lobby.removePlayer(uuid)
    println("closed")
  }

  override def preStart(): Unit = {
    println("connect")
  }

  def killSocket(): Unit = {
    Lobby.removePlayer(uuid)
    self ! PoisonPill
  }
}
