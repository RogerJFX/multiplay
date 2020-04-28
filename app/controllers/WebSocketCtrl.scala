package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import entity.{InMsg, OutMsg}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.api.mvc.WebSocket.MessageFlowTransformer
import services.MyWebSocketActor

@Singleton
class WebSocketCtrl @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  implicit val inEventFormat: OFormat[InMsg] = Json.format[InMsg]
  implicit val outEventFormat: OFormat[OutMsg] = Json.format[OutMsg]
  implicit val messageFlowTransformer: MessageFlowTransformer[InMsg, OutMsg] =
    MessageFlowTransformer.jsonMessageFlowTransformer[InMsg, OutMsg]
  def socket = WebSocket.accept[InMsg, OutMsg] { _ =>
    ActorFlow.actorRef { out =>
      // MyWebSocketActor.props(out)
      Props(new MyWebSocketActor(out))
    }
  }

}

//object MyWebSocketActor {
//  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
//}

//class MyWebSocketActor(out: ActorRef) extends Actor {
//  override def receive = {
//    case msg: InMsg =>
//      println("receive")
//      out ! OutMsg("I received your message: " + msg.msg)
//    case a: Any =>
//      println(a)
//    case _ =>
//      throw new RuntimeException("Not acceptable")
//  }
//
//  override def postStop() = {
//    println("closed")
//  }
//
//  override def preStart(): Unit = {
//    println("connect")
//  }
//}
