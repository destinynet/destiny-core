/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.calendar.Lat.Companion.toLat
import kotlin.test.Test
import kotlin.test.assertEquals

class LatDecoratorEnglishTest {

  private val decorator = LatDecoratorEnglish()

  @Test
  fun getOutputString() {
    assertEquals("01°00'00.000\"N", decorator.getOutputString(1.0.toLat()))
    assertEquals("01°00'00.000\"S", decorator.getOutputString((-1.0).toLat()))
    assertEquals("12°23'38.360\"S", decorator.getOutputString((-12.3939889).toLat()))
  }
}
