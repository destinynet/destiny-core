/**
 * @author smallufo
 * Created on 2007/6/27 at 上午 4:18:15
 */
package destiny.core.astrology

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

    assertEquals("Ecliptic", Coordinate.ECLIPTIC.toString(Locale.ENGLISH))
    assertEquals("Equatorial", Coordinate.EQUATORIAL.toString(Locale.ENGLISH))
    assertEquals("Sidereal", Coordinate.SIDEREAL.toString(Locale.ENGLISH))

    assertEquals("ECLIPTIC", Coordinate.ECLIPTIC.toString())
    assertEquals("EQUATORIAL", Coordinate.EQUATORIAL.toString())
    assertEquals("SIDEREAL", Coordinate.SIDEREAL.toString())

    for (each in Coordinate.values()) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.toString(locale))
      assertNotSame('!', each.toString(locale)[0])
    }
  }
}
