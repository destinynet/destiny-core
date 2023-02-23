/**
 * Created by smallufo on 2023-02-23.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ScoreTest {

  @Test
  fun testRange() {
    assertFailsWith<IllegalArgumentException> {
      Score(1.01)
    }

    assertFailsWith<IllegalArgumentException> {
      Score(-0.01)
    }
  }

  @Test
  fun testSort() {
    val a = Score(0.0)
    val b = Score(0.5)
    val c = Score(0.99)
    val d = Score(1.0)

    assertEquals(listOf(a, b, c, d), setOf(c, b, d, a).sorted())
  }
}
