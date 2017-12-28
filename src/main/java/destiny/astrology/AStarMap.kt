package destiny.astrology

/**
 * Created by smallufo on 2017-12-28.
 */


class AStarMap {

  companion object {
    val numStarMap = mapOf(
      1 to AStar.SUN,
      2 to AStar.MOON
    )

    val numStarLazyMap: Map<Int, AStar> by lazy {
      mapOf(
        1 to AStar.SUN,
        2 to AStar.MOON
      )
    }
  }
}