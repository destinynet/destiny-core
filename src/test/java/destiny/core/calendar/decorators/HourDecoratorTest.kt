/**
 * Created by smallufo on 2017-12-12.
 */
package destiny.core.calendar.decorators

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HourDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("5時", HourDecorator.getOutputString(5, Locale.TAIWAN))
    assertEquals("5时", HourDecorator.getOutputString(5, Locale.SIMPLIFIED_CHINESE))
    assertEquals("5", HourDecorator.getOutputString(5, Locale.ENGLISH))
    assertEquals("5時", HourDecorator.getOutputString(5, Locale.JAPAN)) // 用台灣的版本
  }
}