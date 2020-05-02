package game

import java.util.UUID

import akka.actor.ActorRef
import entity.OutMsg
import game.solitaire.{Lobby, Room}

class Player(val uuid: UUID, val name: String, out: ActorRef) {
  var busy: Boolean = false
  var myRoom: Room = _

  def send(outMsg: OutMsg): Unit = {
    out ! outMsg
  }

  def createRoom(name: String): Option[Room] = {
    if(!busy) {
      busy = true
      val room = new Room(name, this, 4)
      myRoom = room
      Lobby.addRoom(room)
      Some(room)
    } else {
      None
    }
  }

  override def hashCode(): Int = 1

  override def equals(o: Any): Boolean = {
    o.getClass == classOf[Player] && o.asInstanceOf[Player].uuid == uuid
  }
}
