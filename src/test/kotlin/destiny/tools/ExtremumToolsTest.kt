package destiny.tools

import kotlin.math.abs
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtremumToolsTest {

  @Test
  fun minimize() {
    val (x, v) = ExtremumTools.minimize(-10.0, 10.0, 1e-9) { (it - 3.2) * (it - 3.2) + 1 }
    assertEquals(3.2, x, 1e-6)
    assertEquals(1.0, v, 1e-12)
  }

  @Test
  fun maximize() {
    val (x, v) = ExtremumTools.maximize(0.0, 3.0, 1e-9) { sin(it) }
    assertEquals(Math.PI / 2, x, 1e-6)
    assertEquals(1.0, v, 1e-12)
  }

  /** 極值在端點；區間在儒略日量級時，容許度小於浮點精度也要能結束 */
  @Test
  fun edgeAndPrecision() {
    val (x, _) = ExtremumTools.minimize(2_460_000.0, 2_460_001.0, 1e-15) { abs(it - 2_460_000.0) }
    assertTrue(x - 2_460_000.0 < 1e-6)
  }
}
