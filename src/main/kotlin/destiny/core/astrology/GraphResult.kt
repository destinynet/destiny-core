package destiny.core.astrology

import destiny.core.Circular

data class GraphResult(
  val circles: Set<Circular<Planet>>,
  val paths: Set<List<Planet>>,
  val isolated: Set<Planet>,
  /* isolated path terminals */
  val terminals: Set<Planet>
) {
  /** path 的端點，不論是否是 連接上 circle */
  val pathTerminals : Set<Planet>
    get() {
      return paths.map { planets -> planets.last() }.toSet()
    }
}
