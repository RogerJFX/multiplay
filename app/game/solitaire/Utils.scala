package game.solitaire

object Utils {
  def shuffle(n: Int = 52): Array[Int] = {
    val result = Array.fill[Int](n)(-1)
    var candidate = 0
    result.indices.foreach(i => {
      do candidate = (Math.random * n).toInt while (result.contains(candidate))
      result(i) = candidate
    })
    result
  }
}
