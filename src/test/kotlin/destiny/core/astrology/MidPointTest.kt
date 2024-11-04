/**
 * Created by smallufo on 2024-11-04.
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import kotlin.test.Test
import kotlin.test.assertEquals


class MidPointTest {

  @Test
  fun testMidPoint() {
    val mp1 = MidPoint(setOf(Planet.SUN, Planet.MOON), 1.0.toZodiacDegree(), 5)
    val mp2 = MidPoint(setOf(Planet.MOON, Planet.SUN), 1.0.toZodiacDegree(), 5)
    assertEquals(mp1, mp2)

    assertEquals(mp1.p1, mp2.p1)
    assertEquals(mp1.p2, mp2.p2)
  }
}
