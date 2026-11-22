/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

/**
 * DBSCAN 密度分群（純 Kotlin，取代 commons-math3 的 `DBSCANClusterer`）。
 *
 * - 鄰居：與某點距離 ≤ [eps] 的**其他**點（以參照判斷是不是同一點）
 * - 核心點：鄰居數 ≥ [minPts]；從核心點往外擴張，鄰居也是核心點就繼續擴張
 * - 邊界點：不是核心點、但落在某個核心點的鄰域內 → 併入那一群（先被標成雜訊的點，之後仍可被併入）
 * - 雜訊：不屬於任何一群，不出現在結果中
 *
 * 結果的順序與 commons-math3 3.6.1 相同（`DbscanTest` 逐一對照）：群依「第一次遇到核心點」的順序，
 * 群內的點依擴張順序。[distance] 自訂，所以環狀（黃經）距離也適用。
 *
 * @return 每一群的點；沒有任何群時為空串列
 */
fun <T : Any> dbscan(points: Collection<T>, eps: Double, minPts: Int, distance: (T, T) -> Double): List<List<T>> {
  require(eps >= 0) { "eps 不可為負：$eps" }
  require(minPts >= 0) { "minPts 不可為負：$minPts" }

  fun neighborsOf(point: T): List<T> = points.filter { it !== point && distance(it, point) <= eps }

  val inCluster = HashSet<T>()
  val noise = HashSet<T>()
  val clusters = mutableListOf<List<T>>()

  for (point in points) {
    if (point in inCluster || point in noise) continue
    val neighbors = neighborsOf(point)
    if (neighbors.size < minPts) {
      noise += point
      continue
    }

    val cluster = mutableListOf(point)
    inCluster += point
    val seeds = neighbors.toMutableList()
    val seedSet = seeds.toHashSet()
    var index = 0
    while (index < seeds.size) {
      val current = seeds[index]
      val visited = current in inCluster || current in noise
      if (!visited) {
        val currentNeighbors = neighborsOf(current)
        if (currentNeighbors.size >= minPts) {
          currentNeighbors.forEach { if (seedSet.add(it)) seeds += it }
        }
      }
      if (current !in inCluster) {
        noise -= current
        inCluster += current
        cluster += current
      }
      index++
    }
    clusters += cluster
  }
  return clusters
}
