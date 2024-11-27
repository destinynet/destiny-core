/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.calendar.Lat.Companion.toLat
import kotlin.test.Test
import kotlin.test.assertEquals

class LatDecoratorTaiwanTest {

  private val decorator = LatDecoratorTaiwan()

  @Test
  fun getOutputString() {
    assertEquals("北緯：01度00分00.00秒" , decorator.getOutputString((1.0).toLat()))
  }
}
