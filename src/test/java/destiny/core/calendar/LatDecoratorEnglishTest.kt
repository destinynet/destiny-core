/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import kotlin.test.Test
import kotlin.test.assertEquals

class LatDecoratorEnglishTest {

  private val decorator = LatDecoratorEnglish()

  @Test
  fun getOutputString() {
    assertEquals("01°00'00.000\"N" , decorator.getOutputString(1.0))
    assertEquals("01°00'00.000\"S" , decorator.getOutputString(-1.0))
    println(decorator.getOutputString(-12.3939889))
  }
}