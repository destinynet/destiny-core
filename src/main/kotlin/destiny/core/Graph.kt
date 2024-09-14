package destiny.core

data class Graph<T>(
  val circles: Set<Circular<T>>,
  val paths: Set<List<T>>,
  val isolated: Set<T>,
  /* isolated path terminals */
  val terminals: Set<T>
) {
  /** path 的端點，不論是否是 連接上 circle */
  val pathTerminals : Set<T>
    get() {
      return paths.map { planets -> planets.last() }.toSet()
    }
}
