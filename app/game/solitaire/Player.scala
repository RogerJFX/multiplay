package game.solitaire

import java.util.UUID

import akka.actor.ActorRef
import game.{AbstractPlayer, AbstractRoom}

class Player(override val uuid: UUID, override val name: String, out: ActorRef, lobby: Lobby) extends AbstractPlayer(uuid, name, out){

  override var busy: Boolean = false
  override var myRoom: Option[AbstractRoom] = None
  override var waiting: Boolean = false

  def createRoom(name: String): Option[Room] = {
    if(!busy) {
      busy = true
      val room = new Room(name, this, 4, lobby)
      myRoom = Some(room)
      lobby.addRoom(room)
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
