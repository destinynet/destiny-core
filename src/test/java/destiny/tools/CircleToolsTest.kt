/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.tools

import destiny.tools.CircleTools.normalize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CircleToolsTest {

  @Test
  fun testGetNormalizeDegree() {
    //測試大於零的度數
    assertTrue(0.0.normalize() == 0.0)
    assertTrue(359.0.normalize() == 359.0)
    assertTrue(360.0.normalize() == 0.0)
    assertTrue(361.0.normalize() == 1.0)
    assertTrue(720.0.normalize() == 0.0)
    assertTrue(721.0.normalize() == 1.0)

    //測試小於零的度數
    assertTrue((-1.0).normalize() == 359.0)
    assertTrue((-359.0).normalize() == 1.0)
    assertTrue((-360.0).normalize() == 0.0)
    assertTrue((-361.0).normalize() == 359.0)
    assertTrue((-719.0).normalize() == 1.0)
    assertTrue((-720.0).normalize() == 0.0)
    assertTrue((-721.0).normalize() == 359.0)
  }

  @Test
  fun testCenterDegree() {
    assertEquals(45.0, CircleTools.getCenterDegree(0.0, 90.0))
    assertEquals(315.0, CircleTools.getCenterDegree(0.0, 270.0))
    assertEquals(89.5, CircleTools.getCenterDegree(0.0, 179.0))
    assertEquals(270.5, CircleTools.getCenterDegree(0.0, 181.0))

    assertEquals(0.0, CircleTools.getCenterDegree(1.0, 359.0))
    assertEquals(0.0, CircleTools.getCenterDegree(89.0, 271.0))

    assertEquals(90.0, CircleTools.getCenterDegree(89.0, 91.0))
    assertEquals(90.0, CircleTools.getCenterDegree(1.0, 179.0))

    assertEquals(180.0, CircleTools.getCenterDegree(179.0, 181.0))
    assertEquals(180.0, CircleTools.getCenterDegree(91.0, 269.0))

    assertEquals(270.0, CircleTools.getCenterDegree(269.0, 271.0))
    assertEquals(270.0, CircleTools.getCenterDegree(181.0, 359.0))
  }

  @Test
  fun testAheadOf() {
    with(CircleTools) {
      assertEquals(1.0, 1.0.aheadOf(0.0))
      assertEquals(180.0, 180.0.aheadOf(0.0))
      assertEquals(181.0, 181.0.aheadOf(0.0))

      assertEquals(1.0, 0.0.aheadOf(359.0))

      assertEquals(180.0, 180.0.aheadOf(360.0))
      assertEquals(180.0, 360.0.aheadOf(180.0))

      assertEquals(180.0, 270.0.aheadOf(90.0))
      assertEquals(180.0, 90.0.aheadOf(270.0))
    }
  }
}
