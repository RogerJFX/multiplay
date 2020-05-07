package game

import akka.actor.ActorRef
import entity.OutMsg

abstract class AbstractPlayer (out: ActorRef){

  var busy: Boolean
  var myRoom: Option[AbstractRoom]

  def send(outMsg: OutMsg): Unit = {
    out ! outMsg
  }

  def leaveRoom(): Unit = {
    busy = false
    myRoom = None
  }

  def createRoom(name: String): Option[AbstractRoom]

}
