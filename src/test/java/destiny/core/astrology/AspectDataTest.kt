/**
 * Created by smallufo on 2022-08-08.
 */
package destiny.core.astrology

import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.astrology.Planet.MOON
import destiny.core.astrology.Planet.SUN
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class AspectDataTest {

  @Test
  fun testEquals() {
    val p1 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay())
    val p2 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay())
    assertEquals(p1, p2)

    // orb 不影響
    val p3 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.1, null, null, 0.0.toGmtJulDay())
    assertEquals(p1, p3)

    // score 不影響
    val p4 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, 0.1, null, 0.0.toGmtJulDay())
    assertEquals(p1, p4)

    // gmtJulDay 不影響
    val p5 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, 0.1, null, 0.1.toGmtJulDay())
    assertEquals(p1, p5)

    // Type 會影響
    val p6 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, null, Type.APPLYING, 0.1.toGmtJulDay())
    assertNotEquals(p1, p6)
  }
}
