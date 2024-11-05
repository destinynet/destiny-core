/**
 * Created by smallufo on 2020-03-22.
 */
package destiny.tools

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
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

  @ExperimentalTime
  @Test
  @Ignore
  fun measure() {
    measureTimed({ t ->
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
  }

}
