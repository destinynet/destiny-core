/**
 * @author smallufo
 * Created on 2008/6/1 at 上午 6:53:18
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class circleUtilsTest {
  @Test
  fun testGetNormalizeDegree() {
    //測試大於零的度數
    assertTrue(circleUtils.getNormalizeDegree(0.0) == 0.0)
    assertTrue(circleUtils.getNormalizeDegree(359.0) == 359.0)
    assertTrue(circleUtils.getNormalizeDegree(360.0) == 0.0)
    assertTrue(circleUtils.getNormalizeDegree(361.0) == 1.0)
    assertTrue(circleUtils.getNormalizeDegree(720.0) == 0.0)
    assertTrue(circleUtils.getNormalizeDegree(721.0) == 1.0)

    //測試小於零的度數
    assertTrue(circleUtils.getNormalizeDegree(-1.0) == 359.0)
    assertTrue(circleUtils.getNormalizeDegree(-359.0) == 1.0)
    assertTrue(circleUtils.getNormalizeDegree(-360.0) == 0.0)
    assertTrue(circleUtils.getNormalizeDegree(-361.0) == 359.0)
    assertTrue(circleUtils.getNormalizeDegree(-719.0) == 1.0)
    assertTrue(circleUtils.getNormalizeDegree(-720.0) == 0.0)
    assertTrue(circleUtils.getNormalizeDegree(-721.0) == 359.0)
  }

  @Test
  fun testAheadOf() {
    with(circleUtils) {
      assertEquals(1.0 , 1.0.aheadOf(0.0) )
      assertEquals(180.0 , 180.0.aheadOf(0.0) )
      assertEquals(181.0 , 181.0.aheadOf(0.0) )

      assertEquals(1.0 , 0.0.aheadOf(359.0) )

      assertEquals(180.0 , 180.0.aheadOf(360.0) )
      assertEquals(180.0 , 360.0.aheadOf(180.0) )

      assertEquals(180.0 , 270.0.aheadOf(90.0) )
      assertEquals(180.0 , 90.0.aheadOf(270.0) )
    }
  }
}
