/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 3:10:54
 */
package destiny.astrology.classical

import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertSame

class EssentialTriplicityDefaultImplTest {

  @Test
  fun testGetStar() {
    val impl = EssentialTriplicityDefaultImpl()

    assertSame(Planet.SUN, impl.getTriplicityPoint(ARIES, DAY))
    assertSame(Planet.VENUS, impl.getTriplicityPoint(TAURUS, DAY))
    assertSame(Planet.SATURN, impl.getTriplicityPoint(GEMINI, DAY))
    assertSame(Planet.MARS, impl.getTriplicityPoint(CANCER, DAY))
    assertSame(Planet.SUN, impl.getTriplicityPoint(LEO, DAY))
    assertSame(Planet.VENUS, impl.getTriplicityPoint(VIRGO, DAY))
    assertSame(Planet.SATURN, impl.getTriplicityPoint(LIBRA, DAY))
    assertSame(Planet.MARS, impl.getTriplicityPoint(SCORPIO, DAY))
    assertSame(Planet.SUN, impl.getTriplicityPoint(SAGITTARIUS, DAY))
    assertSame(Planet.VENUS, impl.getTriplicityPoint(CAPRICORN, DAY))
    assertSame(Planet.SATURN, impl.getTriplicityPoint(AQUARIUS, DAY))
    assertSame(Planet.MARS, impl.getTriplicityPoint(PISCES, DAY))

    assertSame(Planet.JUPITER, impl.getTriplicityPoint(ARIES, NIGHT))
    assertSame(Planet.MOON, impl.getTriplicityPoint(TAURUS, NIGHT))
    assertSame(Planet.MERCURY, impl.getTriplicityPoint(GEMINI, NIGHT))
    assertSame(Planet.MARS, impl.getTriplicityPoint(CANCER, NIGHT))
    assertSame(Planet.JUPITER, impl.getTriplicityPoint(LEO, NIGHT))
    assertSame(Planet.MOON, impl.getTriplicityPoint(VIRGO, NIGHT))
    assertSame(Planet.MERCURY, impl.getTriplicityPoint(LIBRA, NIGHT))
    assertSame(Planet.MARS, impl.getTriplicityPoint(SCORPIO, NIGHT))
    assertSame(Planet.JUPITER, impl.getTriplicityPoint(SAGITTARIUS, NIGHT))
    assertSame(Planet.MOON, impl.getTriplicityPoint(CAPRICORN, NIGHT))
    assertSame(Planet.MERCURY, impl.getTriplicityPoint(AQUARIUS, NIGHT))
    assertSame(Planet.MARS, impl.getTriplicityPoint(PISCES, NIGHT))
  }

}
