package game.solitaire

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import game.Player

import scala.collection.concurrent.TrieMap

object Lobby {

  private val players = new TrieMap[UUID, Player]()

  def addPlayer(uuid: UUID, player: Player): Option[Player] = players.put(uuid, player)

  def removePlayer(uuid: UUID): Option[Player] = players.remove(uuid)

  def getIdlePlayers: Seq[Player] = {
    players.values.filter(p => !p.busy).toSeq
  }
}
