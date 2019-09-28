/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import kotlin.test.Test
import kotlin.test.assertSame

class TriplicityWilliamImplTest {

  val impl : ITriplicity = TriplicityWilliamImpl()

  @Test
  fun getTriplicityPoint() {
    with(impl) {
      assertSame(SUN, ARIES.getTriplicityPoint(DAY))
      assertSame(VENUS, TAURUS.getTriplicityPoint(DAY))
      assertSame(SATURN, GEMINI.getTriplicityPoint(DAY))
      assertSame(MARS, CANCER.getTriplicityPoint(DAY))
      assertSame(SUN, LEO.getTriplicityPoint(DAY))
      assertSame(VENUS, VIRGO.getTriplicityPoint(DAY))
      assertSame(SATURN, LIBRA.getTriplicityPoint(DAY))
      assertSame(MARS, SCORPIO.getTriplicityPoint(DAY))
      assertSame(SUN, SAGITTARIUS.getTriplicityPoint(DAY))
      assertSame(VENUS, CAPRICORN.getTriplicityPoint(DAY))
      assertSame(SATURN, AQUARIUS.getTriplicityPoint(DAY))
      assertSame(MARS, PISCES.getTriplicityPoint(DAY))

      assertSame(JUPITER, ARIES.getTriplicityPoint(NIGHT))
      assertSame(MOON, TAURUS.getTriplicityPoint(NIGHT))
      assertSame(MERCURY, GEMINI.getTriplicityPoint(NIGHT))
      assertSame(MARS, CANCER.getTriplicityPoint(NIGHT))
      assertSame(JUPITER, LEO.getTriplicityPoint(NIGHT))
      assertSame(MOON, VIRGO.getTriplicityPoint(NIGHT))
      assertSame(MERCURY, LIBRA.getTriplicityPoint(NIGHT))
      assertSame(MARS, SCORPIO.getTriplicityPoint(NIGHT))
      assertSame(JUPITER, SAGITTARIUS.getTriplicityPoint(NIGHT))
      assertSame(MOON, CAPRICORN.getTriplicityPoint(NIGHT))
      assertSame(MERCURY, AQUARIUS.getTriplicityPoint(NIGHT))
      assertSame(MARS, PISCES.getTriplicityPoint(NIGHT))
    }

  }
}
