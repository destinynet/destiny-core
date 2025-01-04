/**
 * Created by smallufo on 2022-08-08.
 */
package destiny.core.astrology

import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.astrology.Planet.*
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.tools.Score.Companion.toScore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

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
    val p4 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, 0.1.toScore(), null, 0.0.toGmtJulDay())
    assertEquals(p1, p4)

    // gmtJulDay 不影響
    val p5 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, 0.1.toScore(), null, 0.1.toGmtJulDay())
    assertEquals(p1, p5)

    // Type 會影響
    val p6 = AspectData.of(SUN, MOON, Aspect.CONJUNCTION, 0.0, null, Type.APPLYING, 0.1.toGmtJulDay())
    assertNotEquals(p1, p6)
  }

  @Test
  fun testOfCollection_ok() {
    val p1 = AspectData.of(listOf(SUN, MOON), Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay())
    val p2 = AspectData.of(listOf(MOON, SUN), Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay())
    assertEquals(p1, p2)

    val p3 = AspectData.of(listOf(SUN, MOON), Aspect.CONJUNCTION, 0.1, null, null, 0.0.toGmtJulDay())
    assertEquals(p1, p3)

    val p4 = AspectData.of(listOf(SUN, MOON), Aspect.CONJUNCTION, 0.0, 0.1.toScore(), null, 0.0.toGmtJulDay())
    assertEquals(p1, p4)

    val p5 = AspectData.of(listOf(SUN, MOON), Aspect.CONJUNCTION, 0.0, null, null, 0.1.toGmtJulDay())
    assertEquals(p1, p5)

    val p6 = AspectData.of(listOf(SUN, MOON), Aspect.CONJUNCTION, 0.0, null, Type.APPLYING, 0.0.toGmtJulDay())
    assertNotEquals(p1, p6)
  }

  @Test
  fun testOfCollection_failed() {
    assertNull(AspectData.of(listOf(SUN, SUN), Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay()))
    assertNull(AspectData.of(listOf(SUN, MOON, MERCURY), Aspect.CONJUNCTION, 0.0, null, null, 0.0.toGmtJulDay()))
  }
}
