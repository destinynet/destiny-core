/**
 * Created by smallufo on 2017-09-18.
 */
package destiny.astrology

import kotlin.test.Test
import kotlin.test.assertTrue

class HoroscopeTest {

  @Test
  fun testGetAngle() {
    assertTrue(Horoscope.getAngle(1.0, 0.0) == 1.0)
    assertTrue(Horoscope.getAngle(179.0, 0.0) == 179.0)
    assertTrue(Horoscope.getAngle(180.0, 0.0) == 180.0)
    assertTrue(Horoscope.getAngle(181.0, 0.0) == 179.0)
    assertTrue(Horoscope.getAngle(359.0, 0.0) == 1.0)

    assertTrue(Horoscope.getAngle(0.0, 1.0) == 1.0)
    assertTrue(Horoscope.getAngle(0.0, 179.0) == 179.0)
    assertTrue(Horoscope.getAngle(0.0, 180.0) == 180.0)
    assertTrue(Horoscope.getAngle(0.0, 181.0) == 179.0)
    assertTrue(Horoscope.getAngle(0.0, 359.0) == 1.0)

    assertTrue(Horoscope.getAngle(270.0, 90.0) == 180.0)
    assertTrue(Horoscope.getAngle(271.0, 90.0) == 179.0)
    assertTrue(Horoscope.getAngle(359.0, 90.0) == 91.0)
    assertTrue(Horoscope.getAngle(0.0, 90.0) == 90.0)
    assertTrue(Horoscope.getAngle(89.0, 90.0) == 1.0)

    assertTrue(Horoscope.getAngle(90.0, 270.0) == 180.0)
    assertTrue(Horoscope.getAngle(90.0, 271.0) == 179.0)
    assertTrue(Horoscope.getAngle(90.0, 359.0) == 91.0)
    assertTrue(Horoscope.getAngle(90.0, 0.0) == 90.0)
    assertTrue(Horoscope.getAngle(90.0, 89.0) == 1.0)
  }

}