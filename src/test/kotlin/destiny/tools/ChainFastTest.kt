/**
 * Created by smallufo on 2020-03-22.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

class ChainFastTest {

  private val logger = KotlinLogging.logger { }

  private val impl: IChainLinks = ChainFast

  @Test
  fun normal() {
    val map = mapOf(
      "A" to "B",
      "B" to "C",
      "C" to "D",
      "E" to "F",
      "F" to "G",
      "H" to "I")

    val expected = setOf(
      listOf("A", "B", "C", "D"),
      listOf("E", "F", "G"),
      listOf("H", "I"))

    assertEquals(expected, impl.chain(map))
  }

  @Test
  fun circular() {
    val map: Map<String, String> = mapOf(
      "A" to "B",
      "B" to "C",
      "C" to "A",
      "D" to "E"
    )

    val expected = setOf(
      listOf("D", "E"))

    assertEquals(expected, impl.chain(map))
  }

  /**
   * 大量資料下的**正確性**與耗時。
   *
   * 測資結構：對每個 `i in 1..99` 造一條 `i → 1000+i → … → (i*10)*1000+i` 的鏈。
   * 所有節點對 1000 取餘都等於 i，因此不同 i 的鏈彼此不相交 ——
   * 結果應恰為 99 條鏈，第 i 條長度為 `i*10+1`。
   *
   * 原本這裡只 log 耗時，演算法把鏈接錯、少接、或多接都不會讓測試變紅。
   */
  @ExperimentalTime
  @Test
  fun measure() {
    val chains = measureTimed({ t ->
      logger.info("$impl takes {} ", t)
    }) {
      val map = (0..99).flatMap { i ->
        (0 until i * 10).map { j ->
          (j * 1000 + i to (j + 1) * 1000 + i)
        }
      }.toMap()
      impl.chain(map)
    }

    assertEquals((1..99).map { it * 10 + 1 }, chains.map { it.size }.sorted())
    assertTrue(chains.contains((0..10).map { it * 1000 + 1 }), "i=1 那條鏈應為 1, 1001, ... , 10001")
  }
}
