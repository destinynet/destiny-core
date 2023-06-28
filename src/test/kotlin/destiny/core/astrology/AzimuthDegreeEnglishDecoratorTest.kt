/**
 * Created by smallufo on 2021-02-13.
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals

internal class AzimuthDegreeEnglishDecoratorTest {

  private val decorator = AzimuthDegreeEnglishDecorator()

  @Test
  fun testCross() {
    assertEquals("North" , decorator.getOutputString(0.0))
    assertEquals("North" , decorator.getOutputString(0.0))
    assertEquals("North" , decorator.getOutputString(0.5))

    assertEquals("East" , decorator.getOutputString(89.5))
    assertEquals("East" , decorator.getOutputString(90.0))
    assertEquals("East" , decorator.getOutputString(90.5))

    assertEquals("South" , decorator.getOutputString(179.5))
    assertEquals("South" , decorator.getOutputString(180.0))
    assertEquals("South" , decorator.getOutputString(180.5))

    assertEquals("West" , decorator.getOutputString(269.5))
    assertEquals("West" , decorator.getOutputString(270.0))
    assertEquals("West" , decorator.getOutputString(270.5))
  }

  @Test
  fun testTilt() {
    assertEquals("E by N 1.0" , decorator.getOutputString(1.0))
    assertEquals("E by N 45.0" , decorator.getOutputString(45.0))

    assertEquals("N by E 44.0" , decorator.getOutputString(46.0))
    assertEquals("N by E 1.0" , decorator.getOutputString(89.0))
    assertEquals("S by E 1.0" , decorator.getOutputString(91.0))
    assertEquals("S by E 45.0" , decorator.getOutputString(135.0))

    assertEquals("E by S 44.0" , decorator.getOutputString(136.0))
    assertEquals("E by S 1.0" , decorator.getOutputString(179.0))
    assertEquals("W by S 1.0" , decorator.getOutputString(181.0))
    assertEquals("W by S 45.0" , decorator.getOutputString(225.0))

    assertEquals("S by W 44.0" , decorator.getOutputString(226.0))
    assertEquals("S by W 1.0" , decorator.getOutputString(269.0))
    assertEquals("N by W 1.0" , decorator.getOutputString(271.0))
    assertEquals("N by W 45.0" , decorator.getOutputString(315.0))

    assertEquals("W by N 44.0" , decorator.getOutputString(316.0))
    assertEquals("W by N 1.0" , decorator.getOutputString(359.0))
  }
}
