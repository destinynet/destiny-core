/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.calendar.Lng.Companion.toLng
import kotlin.test.Test
import kotlin.test.assertEquals

class LngDecoratorEnglishTest {

  private val decorator = LngDecoratorEnglish()

  @Test
  fun getOutputString() {
    assertEquals("121°00'00.00\"E" , decorator.getOutputString(121.0.toLng()))
  }
}
