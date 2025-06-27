/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RulerPtolemyImplTest {

  internal var impl : IRuler = RulerPtolemyImpl


  @Test
  fun `不分日夜 取得 RULER 及 Ruling`() {

    with(impl) {

      assertSame(MARS, ARIES.getRulerPoint())
      assertSame(VENUS, TAURUS.getRulerPoint())
      assertSame(MERCURY, GEMINI.getRulerPoint())
      assertSame(MOON, CANCER.getRulerPoint())
      assertSame(SUN, LEO.getRulerPoint())
      assertSame(MERCURY, VIRGO.getRulerPoint())
      assertSame(VENUS, LIBRA.getRulerPoint())
      assertSame(MARS, SCORPIO.getRulerPoint())
      assertSame(JUPITER, SAGITTARIUS.getRulerPoint())
      assertSame(SATURN, CAPRICORN.getRulerPoint())
      assertSame(SATURN, AQUARIUS.getRulerPoint())
      assertSame(JUPITER, PISCES.getRulerPoint())


      assertTrue(SUN.getRulingSigns().contains(LEO))
      assertTrue(MOON.getRulingSigns().contains(CANCER))
      assertTrue(MERCURY.getRulingSigns().contains(VIRGO))
      assertTrue(MERCURY.getRulingSigns().contains(GEMINI))
      assertTrue(VENUS.getRulingSigns().contains(LIBRA))
      assertTrue(VENUS.getRulingSigns().contains(TAURUS))
      assertTrue(MARS.getRulingSigns().contains(SCORPIO))
      assertTrue(MARS.getRulingSigns().contains(ARIES))
      assertTrue(JUPITER.getRulingSigns().contains(SAGITTARIUS))
      assertTrue(JUPITER.getRulingSigns().contains(PISCES))
      assertTrue(SATURN.getRulingSigns().contains(CAPRICORN))
      assertTrue(SATURN.getRulingSigns().contains(AQUARIUS))
    }
  }


  @Test
  fun `區分日夜 取得 RULER 及 Ruling`() {
    with(impl) {

      assertSame(MARS, ARIES.getRulerPoint(DAY))
      assertNull(ARIES.getRulerPoint(NIGHT)) // 牡羊晚上沒主人

      assertNull(TAURUS.getRulerPoint(DAY))  // 金牛白天沒主人
      assertSame(VENUS, TAURUS.getRulerPoint(NIGHT))

      assertSame(MERCURY, GEMINI.getRulerPoint(DAY))
      assertNull(GEMINI.getRulerPoint(NIGHT)) // 雙子晚上沒主人

      assertSame(MOON, CANCER.getRulerPoint(DAY))   // 巨蟹早晚都是月亮
      assertSame(MOON, CANCER.getRulerPoint(NIGHT))

      assertSame(SUN, LEO.getRulerPoint(DAY))   // 獅子早晚都是太陽
      assertSame(SUN, LEO.getRulerPoint(NIGHT))

      assertNull(VIRGO.getRulerPoint(DAY))       // 處女白天沒主人
      assertSame(MERCURY, VIRGO.getRulerPoint(NIGHT))

      assertSame(VENUS, LIBRA.getRulerPoint(DAY))
      assertNull(LIBRA.getRulerPoint(NIGHT))      // 天秤晚上沒主人

      assertNull(SCORPIO.getRulerPoint(DAY))      // 天蠍白天沒主人
      assertSame(MARS, SCORPIO.getRulerPoint(NIGHT))

      assertSame(JUPITER, SAGITTARIUS.getRulerPoint(DAY))
      assertNull(SAGITTARIUS.getRulerPoint(NIGHT))  // 射手晚上沒主人

      assertNull(CAPRICORN.getRulerPoint(DAY))  // 摩羯白天沒主人
      assertSame(SATURN, CAPRICORN.getRulerPoint(NIGHT))

      assertSame(SATURN, AQUARIUS.getRulerPoint(DAY))
      assertNull(AQUARIUS.getRulerPoint(NIGHT)) // 水瓶晚上沒主人

      assertNull(PISCES.getRulerPoint(DAY))     // 雙魚白天沒主人
      assertSame(JUPITER, PISCES.getRulerPoint(NIGHT))

      assertSame(ARIES, MARS.getRulingSign(DAY))      // 火星 白天掌管 牡羊
      assertSame(SCORPIO, MARS.getRulingSign(NIGHT))  // 火星 晚上掌管 天蠍


      assertSame(LIBRA, VENUS.getRulingSign(DAY))    // 金星 白天掌管 天秤
      assertSame(TAURUS, VENUS.getRulingSign(NIGHT)) // 金星 晚上掌管 金牛

      assertSame(GEMINI, MERCURY.getRulingSign(DAY))   // 水星 白天掌管 雙子
      assertSame(VIRGO, MERCURY.getRulingSign(NIGHT))  // 水星 晚上掌管 處女

      assertSame(CANCER, MOON.getRulingSign(DAY))   // 月亮 白天晚上都掌管 巨蟹
      assertSame(CANCER, MOON.getRulingSign(NIGHT))

      assertSame(LEO, SUN.getRulingSign(DAY))    // 太陽 白天晚上都掌管 獅子
      assertSame(LEO, SUN.getRulingSign(NIGHT))

      assertSame(SAGITTARIUS, JUPITER.getRulingSign(DAY))  // 木星 白天掌管 射手
      assertSame(PISCES, JUPITER.getRulingSign(NIGHT))     // 木星 晚上掌管 雙魚

      assertSame(AQUARIUS, SATURN.getRulingSign(DAY))     // 土星 白天掌管 水瓶
      assertSame(CAPRICORN, SATURN.getRulingSign(NIGHT))  // 土星 晚上掌管 摩羯
    }
  }
}
