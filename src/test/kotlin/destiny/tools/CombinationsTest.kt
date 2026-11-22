/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

import com.google.common.collect.HashBiMap
import com.google.common.collect.Sets
import kotlin.test.*

/**
 * [combinations]／[cartesianProduct] 取代 Guava 的 `Sets.combinations`／`Sets.cartesianProduct`：
 * 不只內容相同，**順序**也要相同 —— 呼叫端有 `toList()[0]`、`[1]` 這類依賴順序的寫法。
 */
class CombinationsTest {

  @Test
  fun `組合：內容與順序都與 Guava 相同`() {
    for (n in 0..9) {
      // 刻意用非排序的元素，確保「組合內順序」跟的是原集合的走訪順序
      val set = (0 until n).map { "e${(it * 7) % 10}" }.toCollection(LinkedHashSet())
      for (k in 0..n) {
        val expected = Sets.combinations(set, k).map { it.toList() }
        val actual = set.combinations(k).map { it.toList() }
        assertEquals(expected, actual, "n=$n k=$k")
      }
    }
  }

  @Test
  fun `組合：可重複走訪`() {
    val c = setOf(1, 2, 3, 4).combinations(2)
    assertEquals(c.toList(), c.toList())
    assertEquals(6, c.count())
  }

  @Test
  fun `組合：k 超出範圍`() {
    assertFailsWith<IllegalArgumentException> { setOf(1, 2).combinations(3) }
    assertFailsWith<IllegalArgumentException> { setOf(1, 2).combinations(-1) }
  }

  @Test
  fun `笛卡兒積：內容與順序都與 Guava 相同`() {
    val a = linkedSetOf("x", "a", "m")
    val b = linkedSetOf("3", "1", "2", "0")
    assertEquals(Sets.cartesianProduct(a, b).toList(), cartesianProduct(a, b))
    assertEquals(Sets.cartesianProduct(a, emptySet()).toList(), cartesianProduct(a, emptySet()))
  }

  /** `AbstractImpls` 由 `HashBiMap` 改成 `LinkedHashMap`：`impls` 的順序（加入順序）不變 */
  @Test
  fun `HashBiMap 的走訪順序就是加入順序`() {
    val keys = (0 until 50).map { "impl${(it * 37) % 101}" }
    val bi = HashBiMap.create<String, Int>()
    val linked = LinkedHashMap<String, Int>()
    keys.forEachIndexed { i, k -> bi[k] = i; linked[k] = i }
    assertEquals(linked.keys.toList(), bi.keys.toList())
  }
}
