/**
 * Created by smallufo on 2020-03-22.
 */
package destiny.tools

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime


class ChainSlowTest {

  private val logger = KotlinLogging.logger { }

  private val impl: IChainLinks = ChainSlow

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
   * 與 [ChainFastTest.measure] 同一份測資、同一組斷言 —— 兩個實作對大資料量必須給出相同結果。
   *
   * 仍維持 `@Ignore`：[ChainSlow] 在這個尺寸下慢到不適合進日常 CI，
   * 但改動 [ChainSlow] 時手動跑它，就能真的驗出對錯（原本只 log 耗時，跑錯也看不出來）。
   */
  @ExperimentalTime
  @Test
  @Ignore
  fun measure() {
    val chains = measureTimed({ t ->
      logger.info("$impl takes {}", t)
    })
    {
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
