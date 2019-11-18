/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:52:20
 */
package destiny.fengshui

import destiny.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AcquiredSymbolCompassTest {

  @Test
  fun testAcquiredSymbolCompass() {
    val compass = AcquiredSymbolCompass()

    assertEquals(337.5, compass.getStartDegree(坎))
    assertEquals(22.5, compass.getStartDegree(艮))

    assertSame(坎, compass.get(0.0))
    assertSame(艮, compass.get(30.0))
    assertSame(離, compass.get(180.0))
    assertSame(兌, compass.get(263.0))
    assertSame(乾, compass.get(337.0))
    assertSame(坎, compass.get(345.0))
    assertSame(坎, compass.get(359.0))

  }
}
