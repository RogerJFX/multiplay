package controllers

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import entity.{InMsg, OutMsg}
import javax.inject.{Inject, Singleton}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.MyWebSocketActor

@Singleton
class WebSocketCtrl @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  implicit val messageFlowTransformer: MessageFlowTransformer[InMsg, OutMsg] =
    MessageFlowTransformer.jsonMessageFlowTransformer[InMsg, OutMsg]

  def socket = WebSocket.accept[InMsg, OutMsg] { _ =>
    ActorFlow.actorRef { out =>
      Props(new MyWebSocketActor(out))
    }
  }
}

