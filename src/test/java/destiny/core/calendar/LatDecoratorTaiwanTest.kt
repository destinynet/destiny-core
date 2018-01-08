/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import kotlin.test.Test

class LatDecoratorTaiwanTest {

  private val decorator = LatDecoratorTaiwan()

  @Test
  fun getOutputString() {
    println(decorator.getOutputString(1.0))
  }
}