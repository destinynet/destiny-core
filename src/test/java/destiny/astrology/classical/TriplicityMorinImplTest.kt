/**
 * Created by smallufo on 2019-09-28.
 */
package destiny.astrology.classical

import destiny.astrology.Element.*
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign
import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import kotlin.test.Test
import kotlin.test.assertSame

class TriplicityMorinImplTest {

  val impl: ITriplicity = TriplicityMorinImpl()

  @Test
  fun getTriplicityPoint() {
    with(impl) {
      // 白天
      // 火象星座
      ZodiacSign.of(FIRE).forEach { sign ->
        assertSame(SUN, sign.getTriplicityPoint(DAY))
      }

      // 土象星座
      ZodiacSign.of(EARTH).forEach { sign ->
        assertSame(MERCURY, sign.getTriplicityPoint(DAY))
      }

      // 風象星座
      ZodiacSign.of(AIR).forEach { sign ->
        assertSame(SATURN, sign.getTriplicityPoint(DAY))
      }

      // 水象星座
      ZodiacSign.of(WATER).forEach { sign ->
        assertSame(JUPITER, sign.getTriplicityPoint(DAY))
      }

      // 夜晚
      // 火象星座
      ZodiacSign.of(FIRE).forEach { sign ->
        assertSame(MARS, sign.getTriplicityPoint(NIGHT))
      }

      // 土象星座
      ZodiacSign.of(EARTH).forEach { sign ->
        assertSame(SATURN, sign.getTriplicityPoint(NIGHT))
      }

      // 風象星座
      ZodiacSign.of(AIR).forEach { sign ->
        assertSame(VENUS, sign.getTriplicityPoint(NIGHT))
      }

      // 水象星座
      ZodiacSign.of(WATER).forEach { sign ->
        assertSame(MOON, sign.getTriplicityPoint(NIGHT))
      }
    }
  }

  @Test
  fun getPartner() {
    with(impl) {
      // 火象星座
      ZodiacSign.of(FIRE).forEach { sign ->
        assertSame(JUPITER, sign.getPartner())
      }

      // 土象星座
      ZodiacSign.of(EARTH).forEach { sign ->
        assertSame(VENUS, sign.getPartner())
      }

      // 風象星座
      ZodiacSign.of(AIR).forEach { sign ->
        assertSame(MERCURY, sign.getPartner())
      }

      // 水象星座
      ZodiacSign.of(WATER).forEach { sign ->
        assertSame(MARS, sign.getPartner())
      }
    }
  }
}
