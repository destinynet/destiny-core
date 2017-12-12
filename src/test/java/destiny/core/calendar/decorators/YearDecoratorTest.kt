/**
 * Created by smallufo on 2017-12-12.
 */
package destiny.core.calendar.decorators

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class YearDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("西元2017年 (民國106年)" , YearDecorator.getOutputString(2017 , Locale.TAIWAN))
    assertEquals("西元2017年" , YearDecorator.getOutputString(2017 , Locale.SIMPLIFIED_CHINESE))
    assertEquals("西暦年2017年" , YearDecorator.getOutputString(2017 , Locale.JAPANESE))
    assertEquals("2017" , YearDecorator.getOutputString(2017 , Locale.ENGLISH))
  }
}