package destiny.core

data class Graph<T>(
  val circles: Set<Circular<T>>,
  val paths: Set<List<T>>,
  val isolated: Set<T>,
  val terminals: Set<T>
) {
  /**
   * 中文：傳回所有 `paths` 的終點。這包含了真正的「最終定位星」(`terminals`)，以及路徑終點所連接到的「互容環」中的行星。
   */
  val pathTerminals : Set<T>
    get() {
      return paths.map { it.last() }.toSet()
    }
}
