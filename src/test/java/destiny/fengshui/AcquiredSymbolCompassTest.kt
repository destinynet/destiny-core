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
    val asc = AcquiredSymbolCompass()

    assertEquals(337.5, asc.getStartDegree(坎))
    assertEquals(22.5, asc.getStartDegree(艮))

    assertSame(坎, asc.getSymbol(0.0))
    assertSame(艮, asc.getSymbol(30.0))
    assertSame(離, asc.getSymbol(180.0))
    assertSame(兌, asc.getSymbol(263.0))
    assertSame(乾, asc.getSymbol(337.0))
    assertSame(坎, asc.getSymbol(345.0))
    assertSame(坎, asc.getSymbol(359.0))

  }
}
