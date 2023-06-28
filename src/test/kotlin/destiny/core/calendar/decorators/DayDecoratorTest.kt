/**
 * Created by smallufo on 2017-12-12.
 */
package destiny.core.calendar.decorators

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DayDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("5日", DayDecorator.getOutputString(5, Locale.TAIWAN))
    assertEquals("5日", DayDecorator.getOutputString(5, Locale.JAPAN))  // 沒有內建日文，採用台灣版本
    assertEquals("5", DayDecorator.getOutputString(5, Locale.ENGLISH))
  }
}