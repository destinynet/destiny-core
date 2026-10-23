/**
 * Created by smallufo on 2026-10-23.
 */
package destiny.tools

import destiny.tools.SampleTools.sampleSteps
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class SampleToolsTest {

  @Test
  fun includesStartAndEnd() {
    assertEquals(listOf(0.0, 2.0, 4.0, 5.0), sampleSteps(5.0, 2.0))
    // 終點剛好落在格點上：不重複
    assertEquals(listOf(0.0, 2.0, 4.0, 6.0), sampleSteps(6.0, 2.0))
  }

  @Test
  fun dropsRegularSampleTooCloseToEnd() {
    // 1440.005 與 1440.0 截成兩位小數後同值 → 捨去 1440.0
    val s = sampleSteps(1440.005, 2.0)
    assertEquals(1438.0, s[s.size - 2])
    assertEquals(1440.005, s.last())
    // 間距足夠則保留
    assertEquals(listOf(1440.0, 1440.02), sampleSteps(1440.02, 2.0).takeLast(2))
  }

  @Test
  fun strictlyIncreasingAndEvenlySpaced() {
    val s = sampleSteps(1439.86, 2.0)
    assertEquals(721, s.size)
    s.zipWithNext().forEach { (a, b) -> assertTrue(b > a) }
    s.dropLast(1).forEachIndexed { i, t -> assertEquals(i * 2.0, t) }
  }

  @Test
  fun degenerateTotals() {
    assertEquals(listOf(0.0), sampleSteps(0.0, 2.0))
    assertEquals(listOf(0.0, 1.0), sampleSteps(1.0, 2.0))
    assertFailsWith<IllegalArgumentException> { sampleSteps(10.0, 0.0) }
    assertFailsWith<IllegalArgumentException> { sampleSteps(-1.0, 2.0) }
  }
}
