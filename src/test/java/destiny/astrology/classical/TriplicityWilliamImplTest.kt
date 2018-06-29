/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertSame

class TriplicityWilliamImplTest {

  val impl = TriplicityWilliamImpl()
  
  @Test
  fun getPoint() {
    assertSame(SUN, impl.getPoint(ARIES, DAY))
    assertSame(VENUS, impl.getPoint(TAURUS, DAY))
    assertSame(SATURN, impl.getPoint(GEMINI, DAY))
    assertSame(MARS, impl.getPoint(CANCER, DAY))
    assertSame(SUN, impl.getPoint(LEO, DAY))
    assertSame(VENUS, impl.getPoint(VIRGO, DAY))
    assertSame(SATURN, impl.getPoint(LIBRA, DAY))
    assertSame(MARS, impl.getPoint(SCORPIO, DAY))
    assertSame(SUN, impl.getPoint(SAGITTARIUS, DAY))
    assertSame(VENUS, impl.getPoint(CAPRICORN, DAY))
    assertSame(SATURN, impl.getPoint(AQUARIUS, DAY))
    assertSame(MARS, impl.getPoint(PISCES, DAY))

    assertSame(JUPITER, impl.getPoint(ARIES, NIGHT))
    assertSame(MOON, impl.getPoint(TAURUS, NIGHT))
    assertSame(MERCURY, impl.getPoint(GEMINI, NIGHT))
    assertSame(MARS, impl.getPoint(CANCER, NIGHT))
    assertSame(JUPITER, impl.getPoint(LEO, NIGHT))
    assertSame(MOON, impl.getPoint(VIRGO, NIGHT))
    assertSame(MERCURY, impl.getPoint(LIBRA, NIGHT))
    assertSame(MARS, impl.getPoint(SCORPIO, NIGHT))
    assertSame(JUPITER, impl.getPoint(SAGITTARIUS, NIGHT))
    assertSame(MOON, impl.getPoint(CAPRICORN, NIGHT))
    assertSame(MERCURY, impl.getPoint(AQUARIUS, NIGHT))
    assertSame(MARS, impl.getPoint(PISCES, NIGHT))
  }
}