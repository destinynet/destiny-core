/**
 * Created by smallufo on 2017-12-13.
 */
package destiny.core.calendar

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LngLatDecoratorTest {

  @Test
  fun getOutputString() {

    val loc = Location.of(Locale.TAIWAN)
    assertEquals("東經 121度 31分 03.60秒, 北緯 25度 2分 20.51秒" , LngLatDecorator.getOutputString(loc , Locale.TAIWAN))
    assertEquals("東經 121度 31分 03.60秒, 北緯 25度 2分 20.51秒" , LngLatDecorator.getOutputString(loc , Locale.JAPANESE))
    assertEquals("东经 121度 31分 03.60秒, 北纬 25度 2分 20.51秒" , LngLatDecorator.getOutputString(loc , Locale.SIMPLIFIED_CHINESE))
    assertEquals("121° 31' 03.60\" E , 25° 2' 20.51\" N" , LngLatDecorator.getOutputString(loc , Locale.ENGLISH))
  }
}