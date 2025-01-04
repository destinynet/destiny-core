/**
 * Created by smallufo on 2022-08-08.
 */
package destiny.core.astrology

import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.astrology.Planet.*
import destiny.tools.Score.Companion.toScore
import kotlin.test.*

internal class PointAspectPatternTest {

  @Test
  fun testEqual1() {
    //測試 equals , 日月對調，必須仍然相等

    val p1 = PointAspectPattern.of(SUN, MOON, Aspect.CONJUNCTION, null, 1.0)
    assertEquals("[?] [太陽, 月亮] CONJUNCTION 誤差  1.0度", p1.brief())
    val p2 = PointAspectPattern.of(MOON, SUN, Aspect.CONJUNCTION, null, 1.0)
    assertEquals("[?] [太陽, 月亮] CONJUNCTION 誤差  1.0度", p1.brief())


    assertEquals(p1, p2)
    assertSame(Aspect.CONJUNCTION, p1.aspect)
    assertSame(Aspect.CONJUNCTION, p2.aspect)
    assertEquals(MOON, p1.getAnotherPoint(SUN))
    assertEquals(SUN, p1.getAnotherPoint(MOON))
    assertEquals(MOON, p2.getAnotherPoint(SUN))
    assertEquals(SUN, p2.getAnotherPoint(MOON))


    val p3 = PointAspectPattern.of(SUN, MARS, Aspect.CONJUNCTION, null, 1.0)
    assertEquals("[?] [太陽, 火星] CONJUNCTION 誤差  1.0度", p3.brief())
    assertTrue(p1 != p3)
    assertEquals(MARS, p3.getAnotherPoint(SUN))
    assertEquals(SUN, p3.getAnotherPoint(MARS))
  }

  /**
   * 不理會 orb
   */
  @Test
  fun testEquals2_Orb() {
    val p1 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, null, 1.0)
    assertEquals("[?] [太陽, 太陽] CONJUNCTION 誤差  1.0度", p1.brief())
    val p2 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, null, 1.0)
    assertEquals(p1, p2)

    // orb 不同，視為 equals , 因其未在 hashCode/equals 考量
    val p3 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, null, 2.0)
    assertEquals(p1, p3)

    assertEquals(SUN, p1.getAnotherPoint(SUN))
  }

  /**
   * 考量 [Type]
   */
  @Test
  fun testEquals3_Type() {
    val p1 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, Type.APPLYING, 1.0)
    val p2 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, Type.APPLYING, 1.0)
    assertEquals(p1, p2)

    val p3 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, null, 1.0)
    assertNotEquals(p1, p3)

    val p4 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, Type.SEPARATING, 1.0)
    assertEquals("[S] [太陽, 太陽] CONJUNCTION 誤差  1.0度", p4.brief())
    assertNotEquals(p1, p4)
  }

  /**
   * score 不在 hashCode/equals 考量
   */
  @Test
  fun testEquals4_Score() {
    val p1 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, Type.APPLYING, 1.0 , 0.1.toScore())
    val p2 = PointAspectPattern.of(SUN, SUN, Aspect.CONJUNCTION, Type.APPLYING, 1.0 , 0.2.toScore())
    assertEquals(p1, p2)
  }


}
