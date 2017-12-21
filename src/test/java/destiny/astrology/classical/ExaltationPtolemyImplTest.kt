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
    assertSame(SUN, exaltImpl.getExaltation(ARIES))
    assertSame(MOON, exaltImpl.getExaltation(TAURUS))
    assertSame(LunarNode.NORTH_TRUE, exaltImpl.getExaltation(GEMINI))
//    assertSame(LunarNode.NORTH_MEAN, exaltImpl.getExaltation(ZodiacSign.GEMINI))
    assertSame(JUPITER, exaltImpl.getExaltation(CANCER))
    assertNull(exaltImpl.getExaltation(LEO))
    assertSame(MERCURY, exaltImpl.getExaltation(VIRGO))
    assertSame(SATURN, exaltImpl.getExaltation(LIBRA))
    assertNull(exaltImpl.getExaltation(SCORPIO))

    assertSame(LunarNode.SOUTH_TRUE, exaltImpl.getExaltation(SAGITTARIUS))
//    assertSame(LunarNode.SOUTH_MEAN, exaltImpl.getExaltation(SAGITTARIUS))
    assertSame(MARS, exaltImpl.getExaltation(CAPRICORN))
    assertNull(exaltImpl.getExaltation(AQUARIUS))
    assertSame(VENUS, exaltImpl.getExaltation(PISCES))
  }

  @Test
  fun `從星體詢問在哪星座 EXALT`() {
    assertSame(ARIES, exaltImpl.getExaltation(SUN))
    assertSame(TAURUS, exaltImpl.getExaltation(MOON))
    assertSame(GEMINI, exaltImpl.getExaltation(LunarNode.NORTH_TRUE))
    assertSame(GEMINI, exaltImpl.getExaltation(LunarNode.NORTH_MEAN))
    assertSame(CANCER, exaltImpl.getExaltation(JUPITER))
    assertSame(VIRGO, exaltImpl.getExaltation(MERCURY))
    assertSame(LIBRA, exaltImpl.getExaltation(SATURN))
    assertSame(SAGITTARIUS, exaltImpl.getExaltation(LunarNode.SOUTH_TRUE))
    assertSame(SAGITTARIUS, exaltImpl.getExaltation(LunarNode.SOUTH_MEAN))
    assertSame(CAPRICORN , exaltImpl.getExaltation(MARS))
    assertSame(PISCES , exaltImpl.getExaltation(VENUS))
  }
}