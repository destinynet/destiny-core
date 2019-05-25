/**
 * Created by smallufo on 2019-05-25.
 */
package destiny.core.calendar

import kotlin.test.Test
import kotlin.test.assertEquals


class LatDecoratorChinaTest {

  private val decorator = LatDecoratorChina()

  @Test
  fun testOutputString() {
    assertEquals("南纬：01度00分00.00秒" , decorator.getOutputString(-1.0))
  }
}