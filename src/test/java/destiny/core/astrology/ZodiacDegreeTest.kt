/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals


class ZodiacDegreeTest {

  @Test
  fun testAheadOf() {
    assertEquals(1.0, ZodiacDegree(1.0).aheadOf(ZodiacDegree(0.0)))
    assertEquals(180.0, ZodiacDegree(180.0).aheadOf(ZodiacDegree(0.0)))
    assertEquals(181.0, ZodiacDegree(181.0).aheadOf(ZodiacDegree(0.0)))
    assertEquals(1.0, ZodiacDegree(0.0).aheadOf(ZodiacDegree(359.0)))
    assertEquals(180.0, ZodiacDegree(180.0).aheadOf(ZodiacDegree(360.0)))
    assertEquals(180.0, ZodiacDegree(360.0).aheadOf(ZodiacDegree(180.0)))
    assertEquals(180.0, ZodiacDegree(270.0).aheadOf(ZodiacDegree(90.0)))
    assertEquals(180.0, ZodiacDegree(90.0).aheadOf(ZodiacDegree(270.0)))
  }
}
