/**
 * @author smallufo
 * Created on 2008/1/27 at 上午 2:38:44
 */
package destiny.core.calendar.eightwords

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class HemisphereByTest {

  @Test
  fun testToString() {
    assertEquals("赤道" , HemisphereBy.EQUATOR.toString(Locale.TAIWAN))
    assertEquals("赤緯" , HemisphereBy.DECLINATION.toString(Locale.TAIWAN))

    assertEquals("赤纬" , HemisphereBy.DECLINATION.toString(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testHemisphereBy() {
    for (each in HemisphereBy.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])

    }
  }
}
