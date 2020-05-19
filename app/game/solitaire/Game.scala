package game.solitaire

import java.util.UUID

import entity.{OutMsg, Task}

import scala.collection.mutable.ListBuffer

class Game(players: ListBuffer[Player]) extends Task {

  private var firstStarted = false
  def run(): Unit = {
    val gameTask = Utils.shuffle().foldLeft[StringBuilder](new StringBuilder)((r, c) => r.append(c + ",")).toString()
    players.foreach(p => {
        p.send(OutMsg("nextGame", now(), gameTask))
    })
  }

  private def tellAllMinusCaller(uuid: UUID, data: String): Unit = {
    players.filter(p => p.uuid != uuid).foreach(p => p.send(OutMsg(TASK_GAME, now(), s"$uuid::$data")))
  }

  private def tellAll(uuid: UUID, data: String): Unit = {
    players.foreach(p => p.send(OutMsg(TASK_GAME, now(), s"$uuid::$data")))
  }

  private def checkAllWaitingAndRun(): Unit = {
    if(players.forall(p => p.waiting)) {
      val roomOpt = players.head.myRoom
      if(roomOpt.isDefined) {
        roomOpt.get.closed = true
        roomOpt.get.broadcastRooms()
        if(!firstStarted) {
          firstStarted = true
          roomOpt.get.broadcastRawMessage("Server", "Game started. Have fun. \uD83E\uDD2A")
        }
        players.foreach(p => p.waiting = false)
      }
      run()
    }
  }

  def takeGameData(uuid: UUID, data: String): Unit = {
    val arr = data.split("::")
    arr(0) match {
      case "points" =>
        tellAllMinusCaller(uuid, data)
      case "next" =>
        val player: Option[Player] = players.find(p => p.uuid == uuid)
        if(player.isDefined) {
          if(!player.get.waiting) {
            player.get.waiting = true
            tellAll(uuid, data)
            checkAllWaitingAndRun()
          }
        }
    }
  }
}
