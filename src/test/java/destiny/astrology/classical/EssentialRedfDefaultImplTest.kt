/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 10:57:16
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.PointDegree
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.EXALTATION
import destiny.astrology.classical.Dignity.FALL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EssentialRedfDefaultImplTest {

  internal var impl = EssentialRedfDefaultImpl(RulerPtolomyImpl() , DetrimentPtolomyImpl())


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
