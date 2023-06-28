/**
 * Created by smallufo on 2017-12-12.
 */
package destiny.core.calendar.decorators

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MinuteDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("5分", MinuteDecorator.getOutputString(5, Locale.TAIWAN))
    assertEquals("5分", MinuteDecorator.getOutputString(5, Locale.SIMPLIFIED_CHINESE))
    assertEquals("5分", MinuteDecorator.getOutputString(5, Locale.JAPANESE)) // 用台灣的版本
    assertEquals("5", MinuteDecorator.getOutputString(5, Locale.ENGLISH))
  }
}