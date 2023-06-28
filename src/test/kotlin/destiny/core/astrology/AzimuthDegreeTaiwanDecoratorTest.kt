/**
 * Created by smallufo on 2021-02-13.
 */
package destiny.core.astrology

import kotlin.test.Test
import kotlin.test.assertEquals

internal class AzimuthDegreeTaiwanDecoratorTest {

  private val decorator = AzimuthDegreeTaiwanDecorator()

  @Test
  fun testCross() {
    assertEquals("正北方" , decorator.getOutputString(0.0))
    assertEquals("正北方" , decorator.getOutputString(0.0))
    assertEquals("正北方" , decorator.getOutputString(0.5))

    assertEquals("正東方" , decorator.getOutputString(89.5))
    assertEquals("正東方" , decorator.getOutputString(90.0))
    assertEquals("正東方" , decorator.getOutputString(90.5))

    assertEquals("正南方" , decorator.getOutputString(179.5))
    assertEquals("正南方" , decorator.getOutputString(180.0))
    assertEquals("正南方" , decorator.getOutputString(180.5))

    assertEquals("正西方" , decorator.getOutputString(269.5))
    assertEquals("正西方" , decorator.getOutputString(270.0))
    assertEquals("正西方" , decorator.getOutputString(270.5))
  }

  @Test
  fun testTile() {
    assertEquals("北偏東1.0度" , decorator.getOutputString(1.0))
    assertEquals("北偏東45.0度" , decorator.getOutputString(45.0))

    assertEquals("東偏北44.0度" , decorator.getOutputString(46.0))
    assertEquals("東偏北1.0度" , decorator.getOutputString(89.0))
    assertEquals("東偏南1.0度" , decorator.getOutputString(91.0))
    assertEquals("東偏南45.0度" , decorator.getOutputString(135.0))

    assertEquals("南偏東44.0度" , decorator.getOutputString(136.0))
    assertEquals("南偏東1.0度" , decorator.getOutputString(179.0))
    assertEquals("南偏西1.0度" , decorator.getOutputString(181.0))
    assertEquals("南偏西45.0度" , decorator.getOutputString(225.0))

    assertEquals("西偏南44.0度" , decorator.getOutputString(226.0))
    assertEquals("西偏南1.0度" , decorator.getOutputString(269.0))
    assertEquals("西偏北1.0度" , decorator.getOutputString(271.0))
    assertEquals("西偏北45.0度" , decorator.getOutputString(315.0))

    assertEquals("北偏西44.0度" , decorator.getOutputString(316.0))
    assertEquals("北偏西1.0度" , decorator.getOutputString(359.0))
  }
}
