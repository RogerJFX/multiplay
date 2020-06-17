package game

import java.util.UUID

import akka.actor.ActorRef
import entity.OutMsg

abstract class AbstractPlayer (val uuid: UUID, val name: String, out: ActorRef){

  var busy: Boolean
  var myRoom: Option[AbstractRoom]
  var waiting: Boolean

  def send(outMsg: OutMsg): Unit = {
    out ! outMsg
  }

  def leaveRoom(): Unit = {
    busy = false
    waiting = false
    myRoom = None
  }

  def createRoom(name: String): Option[AbstractRoom]

}
