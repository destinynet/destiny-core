/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.core.DayNight
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RulerPtolemyImplTest {

  internal var impl = RulerPtolemyImpl()


  @Test
  fun `不分日夜 取得 RULER 及 Ruling`() {
    assertSame(MARS, impl.getPoint(ARIES))
    assertSame(VENUS, impl.getPoint(TAURUS))
    assertSame(MERCURY, impl.getPoint(GEMINI))
    assertSame(MOON, impl.getPoint(CANCER))
    assertSame(SUN, impl.getPoint(LEO))
    assertSame(MERCURY, impl.getPoint(VIRGO))
    assertSame(VENUS, impl.getPoint(LIBRA))
    assertSame(MARS, impl.getPoint(SCORPIO))
    assertSame(JUPITER, impl.getPoint(SAGITTARIUS))
    assertSame(SATURN, impl.getPoint(CAPRICORN))
    assertSame(SATURN, impl.getPoint(AQUARIUS))
    assertSame(JUPITER, impl.getPoint(PISCES))

    assertTrue(impl.getSigns(SUN).contains(LEO))
    assertTrue(impl.getSigns(MOON).contains(CANCER))
    assertTrue(impl.getSigns(MERCURY).contains(VIRGO))
    assertTrue(impl.getSigns(MERCURY).contains(GEMINI))
    assertTrue(impl.getSigns(VENUS).contains(LIBRA))
    assertTrue(impl.getSigns(VENUS).contains(TAURUS))
    assertTrue(impl.getSigns(MARS).contains(SCORPIO))
    assertTrue(impl.getSigns(MARS).contains(ARIES))
    assertTrue(impl.getSigns(JUPITER).contains(SAGITTARIUS))
    assertTrue(impl.getSigns(JUPITER).contains(PISCES))
    assertTrue(impl.getSigns(SATURN).contains(CAPRICORN))
    assertTrue(impl.getSigns(SATURN).contains(AQUARIUS))
  }


  @Test
  fun `區分日夜 取得 RULER 及 Ruling`() {
    assertSame(MARS, impl.getPoint(ARIES, DayNight.DAY))
    assertNull(impl.getPoint(ARIES, DayNight.NIGHT)) // 牡羊晚上沒主人

    assertNull(impl.getPoint(TAURUS, DayNight.DAY))  // 金牛白天沒主人
    assertSame(VENUS, impl.getPoint(TAURUS, DayNight.NIGHT))

    assertSame(MERCURY, impl.getPoint(GEMINI, DayNight.DAY))
    assertNull(impl.getPoint(GEMINI, DayNight.NIGHT)) // 雙子晚上沒主人

    assertSame(MOON, impl.getPoint(CANCER, DayNight.DAY))   // 巨蟹早晚都是月亮
    assertSame(MOON, impl.getPoint(CANCER, DayNight.NIGHT))

    assertSame(SUN, impl.getPoint(LEO, DayNight.DAY))   // 獅子早晚都是太陽
    assertSame(SUN, impl.getPoint(LEO, DayNight.NIGHT))

    assertNull(impl.getPoint(VIRGO, DayNight.DAY))       // 處女白天沒主人
    assertSame(MERCURY, impl.getPoint(VIRGO, DayNight.NIGHT))

    assertSame(VENUS, impl.getPoint(LIBRA, DayNight.DAY))
    assertNull(impl.getPoint(LIBRA, DayNight.NIGHT))      // 天秤晚上沒主人

    assertNull(impl.getPoint(SCORPIO, DayNight.DAY))      // 天蠍白天沒主人
    assertSame(MARS, impl.getPoint(SCORPIO, DayNight.NIGHT))

    assertSame(JUPITER, impl.getPoint(SAGITTARIUS, DayNight.DAY))
    assertNull(impl.getPoint(SAGITTARIUS, DayNight.NIGHT))  // 射手晚上沒主人

    assertNull(impl.getPoint(CAPRICORN, DayNight.DAY))  // 摩羯白天沒主人
    assertSame(SATURN, impl.getPoint(CAPRICORN, DayNight.NIGHT))

    assertSame(SATURN, impl.getPoint(AQUARIUS, DayNight.DAY))
    assertNull(impl.getPoint(AQUARIUS, DayNight.NIGHT)) // 水瓶晚上沒主人

    assertNull(impl.getPoint(PISCES, DayNight.DAY))     // 雙魚白天沒主人
    assertSame(JUPITER, impl.getPoint(PISCES, DayNight.NIGHT))


    MARS.let {
      assertSame(ARIES, impl.getSign(it, DayNight.DAY))      // 火星 白天掌管 牡羊
      assertSame(SCORPIO, impl.getSign(it, DayNight.NIGHT))  // 火星 晚上掌管 天蠍
    }

    VENUS.let {
      assertSame(LIBRA, impl.getSign(it, DayNight.DAY))    // 金星 白天掌管 天秤
      assertSame(TAURUS, impl.getSign(it, DayNight.NIGHT)) // 金星 晚上掌管 金牛
    }

    MERCURY.let {
      assertSame(GEMINI, impl.getSign(it, DayNight.DAY))   // 水星 白天掌管 雙子
      assertSame(VIRGO, impl.getSign(it, DayNight.NIGHT))  // 水星 晚上掌管 處女
    }

    MOON.let {
      assertSame(CANCER, impl.getSign(it, DayNight.DAY))   // 月亮 白天晚上都掌管 巨蟹
      assertSame(CANCER, impl.getSign(it, DayNight.NIGHT))
    }

    SUN.let {
      assertSame(LEO, impl.getSign(it, DayNight.DAY))    // 太陽 白天晚上都掌管 獅子
      assertSame(LEO, impl.getSign(it, DayNight.NIGHT))
    }

    JUPITER.let {
      assertSame(SAGITTARIUS, impl.getSign(it, DayNight.DAY))  // 木星 白天掌管 射手
      assertSame(PISCES, impl.getSign(it, DayNight.NIGHT))     // 木星 晚上掌管 雙魚
    }

    SATURN.let {
      assertSame(AQUARIUS, impl.getSign(it, DayNight.DAY))     // 土星 白天掌管 水瓶
      assertSame(CAPRICORN, impl.getSign(it, DayNight.NIGHT))  // 土星 晚上掌管 摩羯
    }
  }
}