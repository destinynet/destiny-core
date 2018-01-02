/**
 * @author smallufo
 * Created on 2007/6/27 at 上午 4:18:15
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class CoordinateTest {
  @Test
  fun testCoordinate() {
    assertEquals("黃道", Coordinate.ECLIPTIC.toString(Locale.TAIWAN))
    assertEquals("赤道", Coordinate.EQUATORIAL.toString(Locale.TAIWAN))
    assertEquals("恆星", Coordinate.SIDEREAL.toString(Locale.TAIWAN))

    for (each in Coordinate.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
