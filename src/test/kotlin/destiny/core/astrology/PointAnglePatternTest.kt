/**
 * Created by smallufo on 2022-08-07.
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class PointAnglePatternTest {

  @Test
  fun testEquals() {
    val p1 = PointAnglePattern.of(setOf(Planet.SUN, Planet.MOON), 0.0)
    val p2 = PointAnglePattern.of(setOf(Planet.MOON, Planet.SUN), 0.0)
    assertEquals(p1, p2)
  }

  @Test
  fun testNotEquals() {
    val p1 = PointAnglePattern.of(setOf(Planet.SUN, Planet.MOON), 0.0)
    val p2 = PointAnglePattern.of(setOf(Planet.SUN, Planet.MERCURY), 0.0)
    assertNotEquals(p1, p2)
    val p3 = PointAnglePattern.of(setOf(Planet.SUN, Planet.MOON), 30.0)
    assertNotEquals(p1, p3)
  }
}
