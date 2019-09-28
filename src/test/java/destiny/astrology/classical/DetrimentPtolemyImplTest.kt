/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import destiny.core.DayNight
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DetrimentPtolemyImplTest {

  internal var impl = DetrimentPtolemyImpl()

  @Test
  fun `不分日夜，取得在此星座陷落的行星`() {
    with(impl) {
      assertSame(VENUS , ARIES.getDetrimentPoint())
      assertSame(MARS , TAURUS.getDetrimentPoint())
      assertSame(JUPITER, GEMINI.getDetrimentPoint())
      assertSame(SATURN , CANCER.getDetrimentPoint())
      assertSame(SATURN , LEO.getDetrimentPoint())
      assertSame(JUPITER , VIRGO.getDetrimentPoint())
      assertSame(MARS , LIBRA.getDetrimentPoint())
      assertSame(VENUS , SCORPIO.getDetrimentPoint())
      assertSame(MERCURY , SAGITTARIUS.getDetrimentPoint())
      assertSame(MOON , CAPRICORN.getDetrimentPoint())
      assertSame(SUN , AQUARIUS.getDetrimentPoint())
      assertSame(MERCURY , PISCES.getDetrimentPoint())
    }

  }

  @Test
  fun `區分日夜，取得在此星座陷落的行星`() {
    with(impl) {
      assertSame(VENUS , ARIES.getDetrimentPoint(DayNight.DAY))
      assertSame(MARS , TAURUS.getDetrimentPoint(DayNight.NIGHT))
      assertSame(JUPITER, GEMINI.getDetrimentPoint(DayNight.DAY))
      assertSame(SATURN , CANCER.getDetrimentPoint(DayNight.NIGHT))
      //assertSame(SATURN , CANCER.getDetriment(DAY))
      assertSame(SATURN , LEO.getDetrimentPoint(DayNight.DAY))
      //assertSame(SATURN , LEO.getDetriment(NIGHT))
      assertSame(JUPITER , VIRGO.getDetrimentPoint(DayNight.NIGHT))
      assertSame(MARS , LIBRA.getDetrimentPoint(DayNight.DAY))
      assertSame(VENUS , SCORPIO.getDetrimentPoint(DayNight.NIGHT))
      assertSame(MERCURY , SAGITTARIUS.getDetrimentPoint(DayNight.DAY))
      assertSame(MOON , CAPRICORN.getDetrimentPoint(DayNight.NIGHT))
      assertSame(SUN , AQUARIUS.getDetrimentPoint(DayNight.DAY))
      assertSame(MERCURY , PISCES.getDetrimentPoint(DayNight.NIGHT))
    }
  }

  @Test
  fun `不分日夜，取得此行星在哪些星座 陷(-5)`() {
    with(impl) {
      VENUS.getDetrimentSigns().also {
        // 金星 陷 在 卯、戌
        assertTrue(it.contains(ARIES))
        assertTrue(it.contains(SCORPIO))
      }

      MARS.getDetrimentSigns().also {
        // 火星 陷 在 辰、酉
        assertTrue(it.contains(TAURUS))
        assertTrue(it.contains(LIBRA))
      }

      JUPITER.getDetrimentSigns().also {
        // 木星 陷 在 巳、申
        assertTrue(it.contains(GEMINI))
        assertTrue(it.contains(VIRGO))
      }

      MERCURY.getDetrimentSigns().also {
        // 水星 陷 在 寅、亥
        assertTrue(it.contains(SAGITTARIUS))
        assertTrue(it.contains(PISCES))
      }

      SATURN.getDetrimentSigns().also {
        // 土星 陷 在 午、未
        assertTrue(it.contains(CANCER))
        assertTrue(it.contains(LEO))
      }

      SUN.getDetrimentSigns().also {
        // 太陽 陷 在 子
        assertTrue(it.contains(AQUARIUS))
      }

      MOON.getDetrimentSigns().also {
        // 月亮 陷 在 丑
        assertTrue(it.contains(CAPRICORN))
      }
    }

  }
}
