/**
 * Created by smallufo on 2017-12-12.
 */
package destiny.core.calendar.decorators

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MonthDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("五月" , MonthDecorator.getOutputString(5 , Locale.TAIWAN))
    assertEquals("一月" , MonthDecorator.getOutputString(1 , Locale.TAIWAN))
    assertEquals("九月" , MonthDecorator.getOutputString(9 , Locale.TAIWAN))
    assertEquals("五月" , MonthDecorator.getOutputString(5 , Locale.SIMPLIFIED_CHINESE))
    assertEquals("五月" , MonthDecorator.getOutputString(5 , Locale.JAPANESE))  // 用台灣的版本
    assertEquals("May" , MonthDecorator.getOutputString(5 , Locale.ENGLISH))
  }
}