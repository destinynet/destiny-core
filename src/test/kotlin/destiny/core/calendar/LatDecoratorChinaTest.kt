/**
 * Created by smallufo on 2019-05-25.
 */
package destiny.core.calendar

import destiny.core.calendar.LatValue.Companion.toLat
import kotlin.test.Test
import kotlin.test.assertEquals


class LatDecoratorChinaTest {

  private val decorator = LatDecoratorChina()

  @Test
  fun testOutputString() {
    assertEquals("南纬：01度00分00.00秒" , decorator.getOutputString((-1.0).toLat()))
  }
}
