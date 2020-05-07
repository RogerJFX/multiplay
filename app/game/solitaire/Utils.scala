package game.solitaire

object Utils {
  def shuffle(): Array[Int] = {
    val result = Array.ofDim[Int](52)
    @scala.annotation.tailrec
    def rand(i: Int): Unit = {
      val candidate = Math.floor(Math.random * 52).toInt
      if(result.contains(candidate)) {
        rand(i)
      } else {
        result(i) = candidate
      }
    }
    for(i <- result.indices) {
      rand(i)
    }
    result
  }
}
