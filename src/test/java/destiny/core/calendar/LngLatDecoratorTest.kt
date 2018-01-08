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
    assertEquals("東經：121度31分03.60秒, 北緯：25度02分20.51秒" , LngLatDecorator.getOutputString(loc , Locale.TAIWAN))
    assertEquals("東經：121度31分03.60秒, 北緯：25度02分20.51秒" , LngLatDecorator.getOutputString(loc , Locale.JAPANESE))
    assertEquals("東經：121度31分03.60秒, 北緯：25度02分20.51秒" , LngLatDecorator.getOutputString(loc , Locale.JAPAN))

    assertEquals("东经：121度31分03.60秒, 北纬：25度02分20.51秒" , LngLatDecorator.getOutputString(loc , Locale.SIMPLIFIED_CHINESE))
    assertEquals("121°31'03.60\"E, 25°02'20.508\"N" , LngLatDecorator.getOutputString(loc , Locale.ENGLISH))
  }

  @Test
  fun getOutputString_withZero() {
    val loc = Location(12, 3, 4.0, 5, 6, 7.0, TimeZone.getDefault())
    assertEquals("東經：012度03分04.00秒, 北緯：05度06分07.00秒" , LngLatDecorator.getOutputString(loc , Locale.TAIWAN))
    assertEquals("东经：012度03分04.00秒, 北纬：05度06分07.00秒" , LngLatDecorator.getOutputString(loc , Locale.SIMPLIFIED_CHINESE))
  }
}