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

class ExaltationPtolemyImplTest {

  private val exaltImpl: ExaltationPtolemyImpl = ExaltationPtolemyImpl()

  @Test
  fun `從星座找尋 EXALT 的星體`() {
    assertSame(SUN, exaltImpl.getPoint(ARIES))
    assertSame(MOON, exaltImpl.getPoint(TAURUS))
    assertSame(LunarNode.NORTH_TRUE, exaltImpl.getPoint(GEMINI))
//    assertSame(LunarNode.NORTH_MEAN, exaltImpl.getPoint(ZodiacSign.GEMINI))
    assertSame(JUPITER, exaltImpl.getPoint(CANCER))
    assertNull(exaltImpl.getPoint(LEO))
    assertSame(MERCURY, exaltImpl.getPoint(VIRGO))
    assertSame(SATURN, exaltImpl.getPoint(LIBRA))
    assertNull(exaltImpl.getPoint(SCORPIO))

    assertSame(LunarNode.SOUTH_TRUE, exaltImpl.getPoint(SAGITTARIUS))
//    assertSame(LunarNode.SOUTH_MEAN, exaltImpl.getPoint(SAGITTARIUS))
    assertSame(MARS, exaltImpl.getPoint(CAPRICORN))
    assertNull(exaltImpl.getPoint(AQUARIUS))
    assertSame(VENUS, exaltImpl.getPoint(PISCES))
  }

  @Test
  fun `從星體詢問在哪星座 EXALT`() {
    assertSame(ARIES, exaltImpl.getSign(SUN))
    assertSame(TAURUS, exaltImpl.getSign(MOON))
    assertSame(GEMINI, exaltImpl.getSign(LunarNode.NORTH_TRUE))
    assertSame(GEMINI, exaltImpl.getSign(LunarNode.NORTH_MEAN))
    assertSame(CANCER, exaltImpl.getSign(JUPITER))
    assertSame(VIRGO, exaltImpl.getSign(MERCURY))
    assertSame(LIBRA, exaltImpl.getSign(SATURN))
    assertSame(SAGITTARIUS, exaltImpl.getSign(LunarNode.SOUTH_TRUE))
    assertSame(SAGITTARIUS, exaltImpl.getSign(LunarNode.SOUTH_MEAN))
    assertSame(CAPRICORN , exaltImpl.getSign(MARS))
    assertSame(PISCES , exaltImpl.getSign(VENUS))
  }
}