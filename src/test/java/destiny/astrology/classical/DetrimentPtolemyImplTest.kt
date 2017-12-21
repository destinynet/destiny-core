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
import kotlin.test.assertTrue

class DetrimentPtolemyImplTest {

  internal var impl = DetrimentPtolemyImpl()

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

  @Test
  fun `不分日夜，取得此行星在哪些星座 陷(-5)`() {
    impl.getDetriment(VENUS).let {
      // 金星 陷 在 卯、戌
      assertTrue(it.contains(ARIES))
      assertTrue(it.contains(SCORPIO))
    }

    impl.getDetriment(MARS).let {
      // 火星 陷 在 辰、酉
      assertTrue(it.contains(TAURUS))
      assertTrue(it.contains(LIBRA))
    }

    impl.getDetriment(JUPITER).let {
      // 木星 陷 在 巳、申
      assertTrue(it.contains(GEMINI))
      assertTrue(it.contains(VIRGO))
    }

    impl.getDetriment(MERCURY).let {
      // 水星 陷 在 寅、亥
      assertTrue(it.contains(SAGITTARIUS))
      assertTrue(it.contains(PISCES))
    }

    impl.getDetriment(SATURN).let {
      // 土星 陷 在 午、未
      assertTrue(it.contains(CANCER))
      assertTrue(it.contains(LEO))
    }

    impl.getDetriment(SUN).let {
      // 太陽 陷 在 子
      assertTrue(it.contains(AQUARIUS))
    }

    impl.getDetriment(MOON).let {
      // 月亮 陷 在 丑
      assertTrue(it.contains(CAPRICORN))
    }
  }
}