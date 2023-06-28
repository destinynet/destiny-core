/**
 * Created by smallufo on 2020-07-04.
 */
package destiny.core.fengshui

import destiny.core.iching.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class CongenitalSymbolCompassTest {

  val compass = CongenitalSymbolCompass()

  @Test
  fun testCenter() {

    assertEquals(180.0, compass.getCenterDegree(Symbol.乾))
    assertEquals(135.0, compass.getCenterDegree(Symbol.兌))
    assertEquals(90.0, compass.getCenterDegree(Symbol.離))
    assertEquals(45.0, compass.getCenterDegree(Symbol.震))

    assertEquals(225.0, compass.getCenterDegree(Symbol.巽))
    assertEquals(270.0, compass.getCenterDegree(Symbol.坎))
    assertEquals(315.0, compass.getCenterDegree(Symbol.艮))
    assertEquals(0.0, compass.getCenterDegree(Symbol.坤))
  }
}
