package game.solitaire

import java.util.UUID

import entity.{OutMsg, Task}

import scala.collection.mutable.ListBuffer

class Game(players: ListBuffer[Player]) extends Task {
  def run(): Unit = {
    val gameTask = Utils.shuffle()
    players.foreach(p => {
        p.send(OutMsg("nextGame",
          now(),
          gameTask.foldLeft[StringBuilder](new StringBuilder)((r, c) => r.append(c + ",")).toString()))
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
