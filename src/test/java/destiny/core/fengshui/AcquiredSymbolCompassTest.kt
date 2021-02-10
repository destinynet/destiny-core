/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 07:52:20
 */
package destiny.core.fengshui

import destiny.iching.Symbol.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AcquiredSymbolCompassTest {

  val compass = AcquiredSymbolCompass()

  @Test
  fun testAcquiredSymbolCompass() {
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

  @Test
  fun testCenter() {
    assertEquals(0.0 , compass.getCenterDegree(坎))
    assertEquals(45.0 , compass.getCenterDegree(艮))
    assertEquals(90.0 , compass.getCenterDegree(震))
    assertEquals(135.0 , compass.getCenterDegree(巽))

    assertEquals(180.0 , compass.getCenterDegree(離))
    assertEquals(225.0 , compass.getCenterDegree(坤))
    assertEquals(270.0 , compass.getCenterDegree(兌))
    assertEquals(315.0 , compass.getCenterDegree(乾))
  }
}
