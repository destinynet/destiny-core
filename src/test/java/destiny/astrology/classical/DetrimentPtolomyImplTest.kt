/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertSame

class DetrimentPtolomyImplTest {

  internal var impl = DetrimentPtolomyImpl()

  @Test
  fun `不分日夜，取得在此星座陷落的行星`() {
    assertSame(VENUS , impl.getDetriment(ARIES))
    assertSame(MARS , impl.getDetriment(TAURUS))
    assertSame(JUPITER, impl.getDetriment(GEMINI))
    assertSame(SATURN , impl.getDetriment(CANCER))
    assertSame(SATURN , impl.getDetriment(LEO))
    assertSame(JUPITER , impl.getDetriment(VIRGO))
    assertSame(MARS , impl.getDetriment(LIBRA))
    assertSame(VENUS , impl.getDetriment(SCORPIO))
    assertSame(MERCURY , impl.getDetriment(SAGITTARIUS))
    assertSame(MOON , impl.getDetriment(CAPRICORN))
    assertSame(SUN , impl.getDetriment(AQUARIUS))
    assertSame(MERCURY , impl.getDetriment(PISCES))
  }

  @Test
  fun `區分日夜，取得在此星座陷落的行星`() {
    assertSame(VENUS , impl.getDetriment(ARIES , DAY))
    assertSame(MARS , impl.getDetriment(TAURUS , NIGHT))
    assertSame(JUPITER, impl.getDetriment(GEMINI , DAY))
    //assertSame(SATURN , impl.getDetriment(CANCER , DAY))
    assertSame(SATURN , impl.getDetriment(CANCER , NIGHT))
    assertSame(SATURN , impl.getDetriment(LEO , DAY))
    //assertSame(SATURN , impl.getDetriment(LEO , NIGHT))
    assertSame(JUPITER , impl.getDetriment(VIRGO , NIGHT))
    assertSame(MARS , impl.getDetriment(LIBRA , DAY))
    assertSame(VENUS , impl.getDetriment(SCORPIO , NIGHT))
    assertSame(MERCURY , impl.getDetriment(SAGITTARIUS , DAY))
    assertSame(MOON , impl.getDetriment(CAPRICORN , NIGHT))
    assertSame(SUN , impl.getDetriment(AQUARIUS , DAY))
    assertSame(MERCURY , impl.getDetriment(PISCES , NIGHT))
  }
}