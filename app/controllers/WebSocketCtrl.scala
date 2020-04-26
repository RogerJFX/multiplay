package controllers

import akka.actor._
import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import akka.stream.Materializer

@Singleton
class WebSocketCtrl @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  println("HUASSXPIJASDPIHASFD")
  def socket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      MyWebSocketActor.props(out)
    }
  }
}

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("I received your message: " + msg)
  }
}