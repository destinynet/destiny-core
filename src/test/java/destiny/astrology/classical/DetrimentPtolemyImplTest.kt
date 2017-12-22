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
    assertSame(VENUS , impl.getPoint(ARIES))
    assertSame(MARS , impl.getPoint(TAURUS))
    assertSame(JUPITER, impl.getPoint(GEMINI))
    assertSame(SATURN , impl.getPoint(CANCER))
    assertSame(SATURN , impl.getPoint(LEO))
    assertSame(JUPITER , impl.getPoint(VIRGO))
    assertSame(MARS , impl.getPoint(LIBRA))
    assertSame(VENUS , impl.getPoint(SCORPIO))
    assertSame(MERCURY , impl.getPoint(SAGITTARIUS))
    assertSame(MOON , impl.getPoint(CAPRICORN))
    assertSame(SUN , impl.getPoint(AQUARIUS))
    assertSame(MERCURY , impl.getPoint(PISCES))
  }

  @Test
  fun `區分日夜，取得在此星座陷落的行星`() {
    assertSame(VENUS , impl.getPoint(ARIES , DAY))
    assertSame(MARS , impl.getPoint(TAURUS , NIGHT))
    assertSame(JUPITER, impl.getPoint(GEMINI , DAY))
    //assertSame(SATURN , impl.getDetriment(CANCER , DAY))
    assertSame(SATURN , impl.getPoint(CANCER , NIGHT))
    assertSame(SATURN , impl.getPoint(LEO , DAY))
    //assertSame(SATURN , impl.getDetriment(LEO , NIGHT))
    assertSame(JUPITER , impl.getPoint(VIRGO , NIGHT))
    assertSame(MARS , impl.getPoint(LIBRA , DAY))
    assertSame(VENUS , impl.getPoint(SCORPIO , NIGHT))
    assertSame(MERCURY , impl.getPoint(SAGITTARIUS , DAY))
    assertSame(MOON , impl.getPoint(CAPRICORN , NIGHT))
    assertSame(SUN , impl.getPoint(AQUARIUS , DAY))
    assertSame(MERCURY , impl.getPoint(PISCES , NIGHT))
  }

  @Test
  fun `不分日夜，取得此行星在哪些星座 陷(-5)`() {
    impl.getSigns(VENUS).let {
      // 金星 陷 在 卯、戌
      assertTrue(it.contains(ARIES))
      assertTrue(it.contains(SCORPIO))
    }

    impl.getSigns(MARS).let {
      // 火星 陷 在 辰、酉
      assertTrue(it.contains(TAURUS))
      assertTrue(it.contains(LIBRA))
    }

    impl.getSigns(JUPITER).let {
      // 木星 陷 在 巳、申
      assertTrue(it.contains(GEMINI))
      assertTrue(it.contains(VIRGO))
    }

    impl.getSigns(MERCURY).let {
      // 水星 陷 在 寅、亥
      assertTrue(it.contains(SAGITTARIUS))
      assertTrue(it.contains(PISCES))
    }

    impl.getSigns(SATURN).let {
      // 土星 陷 在 午、未
      assertTrue(it.contains(CANCER))
      assertTrue(it.contains(LEO))
    }

    impl.getSigns(SUN).let {
      // 太陽 陷 在 子
      assertTrue(it.contains(AQUARIUS))
    }

    impl.getSigns(MOON).let {
      // 月亮 陷 在 丑
      assertTrue(it.contains(CAPRICORN))
    }
  }
}