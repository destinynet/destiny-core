/**
 * @author smallufo
 * Created on 2008/6/1 at 上午 6:53:18
 */
package destiny.astrology

import kotlin.test.Test
import kotlin.test.assertTrue

class UtilsTest {
  @Test
  fun testGetNormalizeDegree() {
    //測試大於零的度數
    assertTrue(Utils.getNormalizeDegree(0.0) == 0.0)
    assertTrue(Utils.getNormalizeDegree(359.0) == 359.0)
    assertTrue(Utils.getNormalizeDegree(360.0) == 0.0)
    assertTrue(Utils.getNormalizeDegree(361.0) == 1.0)
    assertTrue(Utils.getNormalizeDegree(720.0) == 0.0)
    assertTrue(Utils.getNormalizeDegree(721.0) == 1.0)

    //測試小於零的度數
    assertTrue(Utils.getNormalizeDegree(-1.0) == 359.0)
    assertTrue(Utils.getNormalizeDegree(-359.0) == 1.0)
    assertTrue(Utils.getNormalizeDegree(-360.0) == 0.0)
    assertTrue(Utils.getNormalizeDegree(-361.0) == 359.0)
    assertTrue(Utils.getNormalizeDegree(-719.0) == 1.0)
    assertTrue(Utils.getNormalizeDegree(-720.0) == 0.0)
    assertTrue(Utils.getNormalizeDegree(-721.0) == 359.0)
  }
}
