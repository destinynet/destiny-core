/**
 * @author smallufo
 * Created on 2007/6/27 at 上午 4:18:15
 */
package destiny.core.astrology

import destiny.core.EnumTest
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class CoordinateTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Coordinate::class)
  }

  @Test
  fun testCoordinate() {
    assertEquals("黃道", Coordinate.ECLIPTIC.getTitle(Locale.TAIWAN))
    assertEquals("赤道", Coordinate.EQUATORIAL.getTitle(Locale.TAIWAN))
    assertEquals("恆星", Coordinate.SIDEREAL.getTitle(Locale.TAIWAN))

    assertEquals("Ecliptic", Coordinate.ECLIPTIC.getTitle(Locale.ENGLISH))
    assertEquals("Equatorial", Coordinate.EQUATORIAL.getTitle(Locale.ENGLISH))
    assertEquals("Sidereal", Coordinate.SIDEREAL.getTitle(Locale.ENGLISH))

    for (each in Coordinate.entries) {
      assertNotNull(each.toString())
      assertNotSame('!', each.toString()[0])

      val locale = Locale.ENGLISH
      assertNotNull(each.getTitle(locale))
      assertNotSame('!', each.getTitle(locale)[0])
    }
  }
}
