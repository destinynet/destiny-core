/**
 * Created by smallufo on 2026-11-16.
 */
package destiny.tools

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BisectionToolsTest {

  @Test
  fun `收斂到分界，區間寬是原來的 2 的負 n 次方`() {
    val (lo, hi) = BisectionTools.bisect(1.0, 2.0, 30) { it * it < 2.0 }
    assertTrue(lo * lo < 2.0 && hi * hi >= 2.0)
    assertEquals(sqrt(2.0), hi, 1e-8)
    assertEquals(1.0 / (1L shl 30), hi - lo, 1e-15)
  }

  @Test
  fun `每一輪只問一次，而且問的是中點`() {
    val asked = mutableListOf<Double>()
    val (lo, hi) = BisectionTools.bisect(0.0, 8.0, 3) { asked += it; it < 5.0 }
    assertEquals(listOf(4.0, 6.0, 5.0), asked)
    assertEquals(4.0 to 5.0, lo to hi)
  }

  @Test
  fun `零輪原樣傳回`() {
    assertEquals(3.0 to 7.0, BisectionTools.bisect(3.0, 7.0, 0) { true })
  }
}
