package game

import akka.actor.ActorRef
import entity.OutMsg
import game.solitaire.Room

abstract class AbstractPlayer (out: ActorRef){

  var busy: Boolean // = false
  var myRoom: Option[AbstractRoom] // = None

  def send(outMsg: OutMsg): Unit = {
    out ! outMsg
  }

  def leaveRoom(): Unit = {
    busy = false
    myRoom = None
  }

  def createRoom(name: String): Option[AbstractRoom]

}
