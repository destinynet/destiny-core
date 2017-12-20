/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 10:57:16
 */
package destiny.astrology.classical

import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet.*
import destiny.astrology.PointDegree
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class EssentialRedfDefaultImplTest {

  internal var impl = EssentialRedfDefaultImpl()

  @Test
  fun getRulerByDayNight() {

    assertSame(MARS, impl.getRulerByDayNight(ARIES, DAY))
    assertNull(impl.getRulerByDayNight(ARIES, NIGHT)) // 牡羊晚上沒主人

    assertNull(impl.getRulerByDayNight(TAURUS, DAY))  // 金牛白天沒主人
    assertSame(VENUS, impl.getRulerByDayNight(TAURUS, NIGHT))

    assertSame(MERCURY, impl.getRulerByDayNight(GEMINI, DAY))
    assertNull(impl.getRulerByDayNight(GEMINI, NIGHT)) // 雙子晚上沒主人

    assertSame(MOON, impl.getRulerByDayNight(CANCER, DAY))   // 巨蟹早晚都是月亮
    assertSame(MOON, impl.getRulerByDayNight(CANCER, NIGHT))

    assertSame(SUN, impl.getRulerByDayNight(LEO, DAY))   // 獅子早晚都是太陽
    assertSame(SUN, impl.getRulerByDayNight(LEO, NIGHT))

    assertNull(impl.getRulerByDayNight(VIRGO, DAY))       // 處女白天沒主人
    assertSame(MERCURY, impl.getRulerByDayNight(VIRGO, NIGHT))

    assertSame(VENUS, impl.getRulerByDayNight(LIBRA, DAY))
    assertNull(impl.getRulerByDayNight(LIBRA, NIGHT))      // 天秤晚上沒主人

    assertNull(impl.getRulerByDayNight(SCORPIO, DAY))      // 天蠍白天沒主人
    assertSame(MARS, impl.getRulerByDayNight(SCORPIO, NIGHT))

    assertSame(JUPITER, impl.getRulerByDayNight(SAGITTARIUS, DAY))
    assertNull(impl.getRulerByDayNight(SAGITTARIUS, NIGHT))  // 射手晚上沒主人

    assertNull(impl.getRulerByDayNight(CAPRICORN, DAY))  // 摩羯白天沒主人
    assertSame(SATURN, impl.getRulerByDayNight(CAPRICORN, NIGHT))

    assertSame(SATURN, impl.getRulerByDayNight(AQUARIUS, DAY))
    assertNull(impl.getRulerByDayNight(AQUARIUS, NIGHT)) // 水瓶晚上沒主人

    assertNull(impl.getRulerByDayNight(PISCES, DAY))     // 雙魚白天沒主人
    assertSame(JUPITER, impl.getRulerByDayNight(PISCES, NIGHT))


    MARS.let {
      assertSame(ARIES, impl.getRulingByDayNight(it, DAY))      // 火星 白天掌管 牡羊
      assertSame(SCORPIO, impl.getRulingByDayNight(it, NIGHT))  // 火星 晚上掌管 天蠍
    }

    VENUS.let {
      assertSame(LIBRA, impl.getRulingByDayNight(it, DAY))    // 金星 白天掌管 天秤
      assertSame(TAURUS, impl.getRulingByDayNight(it, NIGHT)) // 金星 晚上掌管 金牛
    }

    MERCURY.let {
      assertSame(GEMINI, impl.getRulingByDayNight(it, DAY))   // 水星 白天掌管 雙子
      assertSame(VIRGO, impl.getRulingByDayNight(it, NIGHT))  // 水星 晚上掌管 處女
    }

    MOON.let {
      assertSame(CANCER, impl.getRulingByDayNight(it, DAY))   // 月亮 白天晚上都掌管 巨蟹
      assertSame(CANCER, impl.getRulingByDayNight(it, NIGHT))
    }

    SUN.let {
      assertSame(LEO, impl.getRulingByDayNight(it, DAY))    // 太陽 白天晚上都掌管 獅子
      assertSame(LEO, impl.getRulingByDayNight(it, NIGHT))
    }

    JUPITER.let {
      assertSame(SAGITTARIUS, impl.getRulingByDayNight(it, DAY))  // 木星 白天掌管 射手
      assertSame(PISCES, impl.getRulingByDayNight(it, NIGHT))     // 木星 晚上掌管 雙魚
    }

    SATURN.let {
      assertSame(AQUARIUS , impl.getRulingByDayNight(it , DAY))     // 土星 白天掌管 水瓶
      assertSame(CAPRICORN , impl.getRulingByDayNight(it , NIGHT))  // 土星 晚上掌管 摩羯
    }

  }

  /** 測試 Ruler (旺)  */
  @Test
  fun testRuler() {
    assertSame(MARS, impl.getPoint(ARIES, RULER))
    assertSame(VENUS, impl.getPoint(TAURUS, RULER))
    assertSame(MERCURY, impl.getPoint(GEMINI, RULER))
    assertSame(MOON, impl.getPoint(CANCER, RULER))
    assertSame(SUN, impl.getPoint(LEO, RULER))
    assertSame(MERCURY, impl.getPoint(VIRGO, RULER))
    assertSame(VENUS, impl.getPoint(LIBRA, RULER))
    assertSame(MARS, impl.getPoint(SCORPIO, RULER))
    assertSame(JUPITER, impl.getPoint(SAGITTARIUS, RULER))
    assertSame(SATURN, impl.getPoint(CAPRICORN, RULER))
    assertSame(SATURN, impl.getPoint(AQUARIUS, RULER))
    assertSame(JUPITER, impl.getPoint(PISCES, RULER))
  }

  /** 測試 Detriment (陷) , 其值為對沖星座之Ruler  */
  @Test
  fun testDetriment() {
    assertSame(VENUS, impl.getPoint(ARIES, DETRIMENT))
    assertSame(MARS, impl.getPoint(TAURUS, DETRIMENT))
    assertSame(JUPITER, impl.getPoint(GEMINI, DETRIMENT))
    assertSame(SATURN, impl.getPoint(CANCER, DETRIMENT))
    assertSame(SATURN, impl.getPoint(LEO, DETRIMENT))
    assertSame(JUPITER, impl.getPoint(VIRGO, DETRIMENT))
    assertSame(MARS, impl.getPoint(LIBRA, DETRIMENT))
    assertSame(VENUS, impl.getPoint(SCORPIO, DETRIMENT))
    assertSame(MERCURY, impl.getPoint(SAGITTARIUS, DETRIMENT))
    assertSame(MOON, impl.getPoint(CAPRICORN, DETRIMENT))
    assertSame(SUN, impl.getPoint(AQUARIUS, DETRIMENT))
    assertSame(MERCURY, impl.getPoint(PISCES, DETRIMENT))
  }

  /** 測試 Exaltation (廟)  */
  @Test
  fun testExaltation() {
    assertSame(SUN, impl.getPoint(ARIES, EXALTATION))
    assertSame(MOON, impl.getPoint(TAURUS, EXALTATION))
    //assertSame(LunarNode.NORTH , impl.getPoint(ZodiacSign.GEMINI      , Dignity.EXALTATION));
    assertSame(JUPITER, impl.getPoint(CANCER, EXALTATION))
    assertSame(null, impl.getPoint(LEO, EXALTATION))
    assertSame(MERCURY, impl.getPoint(VIRGO, EXALTATION))
    assertSame(SATURN, impl.getPoint(LIBRA, EXALTATION))
    assertSame(null, impl.getPoint(SCORPIO, EXALTATION))
    //assertSame(LunarNode.SOUTH , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.EXALTATION));
    assertSame(MARS, impl.getPoint(CAPRICORN, EXALTATION))
    assertSame(null, impl.getPoint(AQUARIUS, EXALTATION))
    assertSame(VENUS, impl.getPoint(PISCES, EXALTATION))
  }

  /** 測試 Fall (落) , 其值為對沖星座之Exaltation之星體  */
  @Test
  fun testFall() {
    assertSame(SATURN, impl.getPoint(ARIES, FALL))
    assertSame(null, impl.getPoint(TAURUS, FALL))
    //assertSame(LunarNode.SOUTH   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.FALL));
    assertSame(MARS, impl.getPoint(CANCER, FALL))
    assertSame(null, impl.getPoint(LEO, FALL))
    assertSame(VENUS, impl.getPoint(VIRGO, FALL))
    assertSame(SUN, impl.getPoint(LIBRA, FALL))
    assertSame(MOON, impl.getPoint(SCORPIO, FALL))
    //assertSame(LunarNode.NORTH   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.FALL));
    assertSame(JUPITER, impl.getPoint(CAPRICORN, FALL))
    assertSame(null, impl.getPoint(AQUARIUS, FALL))
    assertSame(MERCURY, impl.getPoint(PISCES, FALL))
  }


  /**
   * 測試星座的 Exaltation 星體及度數
   */
  @Test
  fun testGetExaltationStarDegree() {
    assertEquals(PointDegree(SUN, ARIES, 19.0), impl.getExaltationStarDegree(ARIES))
    assertEquals(PointDegree(MOON, TAURUS, 3.0), impl.getExaltationStarDegree(TAURUS))
    //assertEquals( new PointDegree(LunarNode.NORTH , ZodiacSign.GEMINI      ,  3), impl.getExaltationStarDegree(ZodiacSign.GEMINI     ));
    assertEquals(PointDegree(JUPITER, CANCER, 15.0), impl.getExaltationStarDegree(CANCER))
    assertEquals(null, impl.getExaltationStarDegree(LEO))
    assertEquals(PointDegree(MERCURY, VIRGO, 15.0), impl.getExaltationStarDegree(VIRGO))
    assertEquals(PointDegree(SATURN, LIBRA, 21.0), impl.getExaltationStarDegree(LIBRA))
    assertEquals(null, impl.getExaltationStarDegree(SCORPIO))
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.SAGITTARIUS ,  3), impl.getExaltationStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals(PointDegree(MARS, CAPRICORN, 28.0), impl.getExaltationStarDegree(CAPRICORN))
    assertEquals(null, impl.getExaltationStarDegree(AQUARIUS))
    assertEquals(PointDegree(VENUS, PISCES, 27.0), impl.getExaltationStarDegree(PISCES))
  }

  @Test
  fun testGetFallStarDegree() {
    assertEquals(PointDegree(SATURN, ARIES, 21.0), impl.getFallStarDegree(ARIES))
    assertEquals(null, impl.getFallStarDegree(TAURUS))
    //assertEquals( new PointDegree(LunarNode.SOUTH , ZodiacSign.GEMINI      ,  3), impl.getFallStarDegree(ZodiacSign.GEMINI     ));
    assertEquals(PointDegree(MARS, CANCER, 28.0), impl.getFallStarDegree(CANCER))
    assertEquals(null, impl.getFallStarDegree(LEO))
    assertEquals(PointDegree(VENUS, VIRGO, 27.0), impl.getFallStarDegree(VIRGO))
    assertEquals(PointDegree(SUN, LIBRA, 19.0), impl.getFallStarDegree(LIBRA))
    assertEquals(PointDegree(MOON, SCORPIO, 3.0), impl.getFallStarDegree(SCORPIO))
    //assertEquals( new PointDegree(LunarNode.NORTH     , ZodiacSign.SAGITTARIUS ,  3), impl.getFallStarDegree(ZodiacSign.SAGITTARIUS));
    assertEquals(PointDegree(JUPITER, CAPRICORN, 15.0), impl.getFallStarDegree(CAPRICORN))
    assertEquals(null, impl.getFallStarDegree(AQUARIUS))
    assertEquals(PointDegree(MERCURY, PISCES, 15.0), impl.getFallStarDegree(PISCES))

  }

}
