/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.calendar.LngValue.Companion.toLng
import kotlin.test.Test
import kotlin.test.assertEquals

class LngDecoratorTaiwanTest {

  private val decorator = LngDecoratorTaiwan()

  @Test
  fun getOutputString() {
    assertEquals("東經：121度00分00.00秒" , decorator.getOutputString(121.0.toLng()))
    assertEquals("東經：121度30分00.00秒" , decorator.getOutputString(121.50.toLng()))
    assertEquals("東經：121度30分36.00秒" , decorator.getOutputString(121.51.toLng()))
    assertEquals("西經：121度30分36.00秒" , decorator.getOutputString((-121.51).toLng()))
    assertEquals("東經：001度00分00.00秒" , decorator.getOutputString(1.0.toLng()))
    assertEquals("西經：010度00分00.00秒" , decorator.getOutputString((-10.0).toLng()))
  }
}
