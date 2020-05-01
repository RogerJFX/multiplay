package game.solitaire

import java.util.UUID

import entity.OutMsg
import game.Player

import scala.collection.concurrent.TrieMap

object Lobby {

  private val playerMap = new TrieMap[UUID, Player]()

  def addPlayer(uuid: UUID, player: Player): Option[Player] = {
    val opt = playerMap.put(uuid, player)
    broadcastCount()
    opt
  }

  def removePlayer(uuid: UUID): Option[Player] = {
    val opt = playerMap.remove(uuid)
    broadcastCount()
    opt
  }

  def getIdlePlayers: Seq[Player] = {
    playerMap.values.filter(p => !p.busy).toSeq
  }

  def broadcastCount() = {
    val c = playerMap.size
    playerMap.values.foreach(p => p.send(OutMsg("count", 0, s"""{count: $c}""")))
  }
}
