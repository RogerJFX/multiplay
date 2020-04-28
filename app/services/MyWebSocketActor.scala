package services

import akka.actor.{Actor, ActorRef}
import entity.{InMsg, OutMsg}

class MyWebSocketActor (out: ActorRef) extends Actor {
  override def receive = {
    case msg: InMsg =>
      println("receive")
      out ! OutMsg("I received your message: " + msg.msg)
    case a: Any =>
      println(a)
    case _ =>
      throw new RuntimeException("Not acceptable")
  }

  override def postStop() = {
    println("closed")
  }

  override def preStart(): Unit = {
    println("connect")
  }
}
