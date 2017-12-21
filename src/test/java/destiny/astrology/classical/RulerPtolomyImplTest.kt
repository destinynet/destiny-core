/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RulerPtolomyImplTest {

  internal var impl = RulerPtolomyImpl()


  @Test
  fun `不分日夜 取得 RULER 及 Ruling`() {
    assertSame(MARS, impl.getRuler(ARIES))
    assertSame(VENUS, impl.getRuler(TAURUS))
    assertSame(MERCURY, impl.getRuler(GEMINI))
    assertSame(MOON, impl.getRuler(CANCER))
    assertSame(SUN, impl.getRuler(LEO))
    assertSame(MERCURY, impl.getRuler(VIRGO))
    assertSame(VENUS, impl.getRuler(LIBRA))
    assertSame(MARS, impl.getRuler(SCORPIO))
    assertSame(JUPITER, impl.getRuler(SAGITTARIUS))
    assertSame(SATURN, impl.getRuler(CAPRICORN))
    assertSame(SATURN, impl.getRuler(AQUARIUS))
    assertSame(JUPITER, impl.getRuler(PISCES))

    assertTrue(impl.getRuling(SUN).contains(LEO))
    assertTrue(impl.getRuling(MOON).contains(CANCER))
    assertTrue(impl.getRuling(MERCURY).contains(VIRGO))
    assertTrue(impl.getRuling(MERCURY).contains(GEMINI))
    assertTrue(impl.getRuling(VENUS).contains(LIBRA))
    assertTrue(impl.getRuling(VENUS).contains(TAURUS))
    assertTrue(impl.getRuling(MARS).contains(SCORPIO))
    assertTrue(impl.getRuling(MARS).contains(ARIES))
    assertTrue(impl.getRuling(JUPITER).contains(SAGITTARIUS))
    assertTrue(impl.getRuling(JUPITER).contains(PISCES))
    assertTrue(impl.getRuling(SATURN).contains(CAPRICORN))
    assertTrue(impl.getRuling(SATURN).contains(AQUARIUS))
  }


  @Test
  fun `區分日夜 取得 RULER 及 Ruling`() {
    assertSame(MARS, impl.getRuler(ARIES, DayNight.DAY))
    assertNull(impl.getRuler(ARIES, DayNight.NIGHT)) // 牡羊晚上沒主人

    assertNull(impl.getRuler(TAURUS, DayNight.DAY))  // 金牛白天沒主人
    assertSame(VENUS, impl.getRuler(TAURUS, DayNight.NIGHT))

    assertSame(MERCURY, impl.getRuler(GEMINI, DayNight.DAY))
    assertNull(impl.getRuler(GEMINI, DayNight.NIGHT)) // 雙子晚上沒主人

    assertSame(MOON, impl.getRuler(CANCER, DayNight.DAY))   // 巨蟹早晚都是月亮
    assertSame(MOON, impl.getRuler(CANCER, DayNight.NIGHT))

    assertSame(SUN, impl.getRuler(LEO, DayNight.DAY))   // 獅子早晚都是太陽
    assertSame(SUN, impl.getRuler(LEO, DayNight.NIGHT))

    assertNull(impl.getRuler(VIRGO, DayNight.DAY))       // 處女白天沒主人
    assertSame(MERCURY, impl.getRuler(VIRGO, DayNight.NIGHT))

    assertSame(VENUS, impl.getRuler(LIBRA, DayNight.DAY))
    assertNull(impl.getRuler(LIBRA, DayNight.NIGHT))      // 天秤晚上沒主人

    assertNull(impl.getRuler(SCORPIO, DayNight.DAY))      // 天蠍白天沒主人
    assertSame(MARS, impl.getRuler(SCORPIO, DayNight.NIGHT))

    assertSame(JUPITER, impl.getRuler(SAGITTARIUS, DayNight.DAY))
    assertNull(impl.getRuler(SAGITTARIUS, DayNight.NIGHT))  // 射手晚上沒主人

    assertNull(impl.getRuler(CAPRICORN, DayNight.DAY))  // 摩羯白天沒主人
    assertSame(SATURN, impl.getRuler(CAPRICORN, DayNight.NIGHT))

    assertSame(SATURN, impl.getRuler(AQUARIUS, DayNight.DAY))
    assertNull(impl.getRuler(AQUARIUS, DayNight.NIGHT)) // 水瓶晚上沒主人

    assertNull(impl.getRuler(PISCES, DayNight.DAY))     // 雙魚白天沒主人
    assertSame(JUPITER, impl.getRuler(PISCES, DayNight.NIGHT))


    MARS.let {
      assertSame(ARIES, impl.getRuling(it, DayNight.DAY))      // 火星 白天掌管 牡羊
      assertSame(SCORPIO, impl.getRuling(it, DayNight.NIGHT))  // 火星 晚上掌管 天蠍
    }

    VENUS.let {
      assertSame(LIBRA, impl.getRuling(it, DayNight.DAY))    // 金星 白天掌管 天秤
      assertSame(TAURUS, impl.getRuling(it, DayNight.NIGHT)) // 金星 晚上掌管 金牛
    }

    MERCURY.let {
      assertSame(GEMINI, impl.getRuling(it, DayNight.DAY))   // 水星 白天掌管 雙子
      assertSame(VIRGO, impl.getRuling(it, DayNight.NIGHT))  // 水星 晚上掌管 處女
    }

    MOON.let {
      assertSame(CANCER, impl.getRuling(it, DayNight.DAY))   // 月亮 白天晚上都掌管 巨蟹
      assertSame(CANCER, impl.getRuling(it, DayNight.NIGHT))
    }

    SUN.let {
      assertSame(LEO, impl.getRuling(it, DayNight.DAY))    // 太陽 白天晚上都掌管 獅子
      assertSame(LEO, impl.getRuling(it, DayNight.NIGHT))
    }

    JUPITER.let {
      assertSame(SAGITTARIUS, impl.getRuling(it, DayNight.DAY))  // 木星 白天掌管 射手
      assertSame(PISCES, impl.getRuling(it, DayNight.NIGHT))     // 木星 晚上掌管 雙魚
    }

    SATURN.let {
      assertSame(AQUARIUS, impl.getRuling(it, DayNight.DAY))     // 土星 白天掌管 水瓶
      assertSame(CAPRICORN, impl.getRuling(it, DayNight.NIGHT))  // 土星 晚上掌管 摩羯
    }
  }
}