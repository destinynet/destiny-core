/**
 * Created by smallufo on 2019-05-25.
 */
package destiny.core.calendar

import destiny.core.calendar.Lng.Companion.toLng
import kotlin.test.Test
import kotlin.test.assertEquals

class LngDecoratorChinaTest {

  private val decorator = LngDecoratorChina()

  @Test
  fun getOutputString() {
    assertEquals("东经：121度00分00.00秒" , decorator.getOutputString(121.0.toLng()))
    assertEquals("东经：121度30分00.00秒" , decorator.getOutputString(121.50.toLng()))
    assertEquals("东经：121度30分36.00秒" , decorator.getOutputString(121.51.toLng()))
    assertEquals("西经：121度30分36.00秒" , decorator.getOutputString((-121.51).toLng()))
    assertEquals("东经：001度00分00.00秒" , decorator.getOutputString(1.0.toLng()))
    assertEquals("西经：010度00分00.00秒" , decorator.getOutputString((-10.0).toLng()))
  }
}
