/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

/**
 * 從集合中取 [k] 個元素的所有組合（純 Kotlin，取代 Guava `Sets.combinations`）。
 *
 * 順序與 Guava 相同（`CombinationsTest` 逐一對照）：
 *  - 每個組合內的元素，依原集合的走訪順序排列
 *  - 組合之間的先後，依「位元組合」遞增：先 {0,1,…,k−1}，每次把最低一段連續的 1 往上推一格
 *
 * 惰性產生；元素數 n、取 k 個，共 C(n, k) 組。
 */
fun <T> Set<T>.combinations(k: Int): Iterable<Set<T>> {
  require(k in 0..size) { "k 必須介於 0 與 $size 之間：$k" }
  val elements = this.toList()
  val n = elements.size
  if (k == 0) return listOf(emptySet())
  if (k == n) return listOf(this)

  return Iterable {
    iterator {
      // bits[i] ＝ 第 i 個元素有沒有被選中
      val bits = BooleanArray(n) { it < k }
      while (true) {
        yield(elements.filterIndexedTo(LinkedHashSet(k)) { i, _ -> bits[i] })

        val firstSet = bits.indexOfFirst { it }
        var toFlip = firstSet
        while (toFlip < n && bits[toFlip]) toFlip++
        if (toFlip == n) break
        // 目前是 {firstSet, …, toFlip−1, …}（不含 toFlip）；下一組是 {0, …, toFlip−firstSet−2, toFlip, …}
        bits[toFlip] = true
        for (i in firstSet until toFlip) bits[i] = false
        for (i in 0 until toFlip - firstSet - 1) bits[i] = true
      }
    }
  }
}

/**
 * 兩個集合的笛卡兒積，每一組是 `[a, b]`（純 Kotlin，取代 Guava 的兩參數 `Sets.cartesianProduct`）。
 * 順序與 Guava 相同：[first] 在外層、[second] 在內層。
 */
fun <T> cartesianProduct(first: Set<T>, second: Set<T>): List<List<T>> =
  first.flatMap { a -> second.map { b -> listOf(a, b) } }
