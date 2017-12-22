/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.LunarNode
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class FallPtolemyImplTest {

  private val fallImpl = FallPtolemyImpl()

  @Test
  fun getPoint() {
    assertSame(SATURN, fallImpl.getPoint(ARIES))
    assertNull(fallImpl.getPoint(TAURUS))
    assertSame(LunarNode.SOUTH_TRUE, fallImpl.getPoint(GEMINI))
    //assertSame(LunarNode.SOUTH_MEAN , fallImpl.getPoint(GEMINI))
    assertSame(MARS, fallImpl.getPoint(CANCER))
    assertNull(fallImpl.getPoint(LEO))
    assertSame(VENUS, fallImpl.getPoint(VIRGO))
    assertSame(SUN, fallImpl.getPoint(LIBRA))
    assertSame(MOON, fallImpl.getPoint(SCORPIO))
    assertSame(LunarNode.NORTH_TRUE, fallImpl.getPoint(SAGITTARIUS))
    //assertSame(LunarNode.NORTH_MEAN, fallImpl.getPoint(SAGITTARIUS))
    assertSame(JUPITER, fallImpl.getPoint(CAPRICORN))
    assertNull(fallImpl.getPoint(AQUARIUS))
    assertSame(MERCURY, fallImpl.getPoint(PISCES))
  }

  @Test
  fun getSign() {
    assertSame(ARIES, fallImpl.getSign(SATURN))
    assertSame(GEMINI, fallImpl.getSign(LunarNode.SOUTH_TRUE))
    assertSame(GEMINI, fallImpl.getSign(LunarNode.SOUTH_MEAN))
    assertSame(CANCER, fallImpl.getSign(MARS))
    assertSame(VIRGO, fallImpl.getSign(VENUS))
    assertSame(LIBRA, fallImpl.getSign(SUN))
    assertSame(SCORPIO, fallImpl.getSign(MOON))
    assertSame(SAGITTARIUS, fallImpl.getSign(LunarNode.NORTH_TRUE))
    assertSame(SAGITTARIUS, fallImpl.getSign(LunarNode.NORTH_MEAN))
    assertSame(CAPRICORN, fallImpl.getSign(JUPITER))
    assertSame(PISCES, fallImpl.getSign(MERCURY))

  }
}