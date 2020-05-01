package game

import java.util.UUID

import akka.actor.ActorRef
import entity.OutMsg

class Player(val uuid: UUID, val name: String, out: ActorRef) {
  var busy: Boolean = false

  def send(outMsg: OutMsg): Unit = {
    out ! outMsg
  }
}
