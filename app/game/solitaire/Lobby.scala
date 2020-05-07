package game.solitaire

import java.util.UUID

import entity.Task
import game.AbstractLobby
import javax.inject.Singleton
import util.SimpleJsonParser

@Singleton
class Lobby extends AbstractLobby with SimpleJsonParser with Task {

  override def getPlayer(uuid: UUID): Option[Player] = playerMap.get(uuid).asInstanceOf[Option[Player]]

}
