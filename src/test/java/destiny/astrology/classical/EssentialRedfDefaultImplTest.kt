/**
 * @author smallufo
 * Created on 2007/11/26 at 下午 10:57:16
 */
package destiny.astrology.classical

import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.EXALTATION
import destiny.astrology.classical.Dignity.FALL
import kotlin.test.Test
import kotlin.test.assertSame

class EssentialRedfDefaultImplTest {

  internal var impl = EssentialRedfDefaultImpl(RulerPtolemyImpl() , DetrimentPtolemyImpl() , ExaltationPtolemyImpl() , FallPtolemyImpl())


  /** 測試 Exaltation (廟)  */
  @Test
  fun testExaltation() {
    assertSame(SUN, impl.getPointOld(ARIES, EXALTATION))
    assertSame(MOON, impl.getPointOld(TAURUS, EXALTATION))
    //assertSame(LunarNode.NORTH , impl.getPoint(ZodiacSign.GEMINI      , Dignity.EXALTATION));
    assertSame(JUPITER, impl.getPointOld(CANCER, EXALTATION))
    assertSame(null, impl.getPointOld(LEO, EXALTATION))
    assertSame(MERCURY, impl.getPointOld(VIRGO, EXALTATION))
    assertSame(SATURN, impl.getPointOld(LIBRA, EXALTATION))
    assertSame(null, impl.getPointOld(SCORPIO, EXALTATION))
    //assertSame(LunarNode.SOUTH , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.EXALTATION));
    assertSame(MARS, impl.getPointOld(CAPRICORN, EXALTATION))
    assertSame(null, impl.getPointOld(AQUARIUS, EXALTATION))
    assertSame(VENUS, impl.getPointOld(PISCES, EXALTATION))
  }

  /** 測試 Fall (落) , 其值為對沖星座之Exaltation之星體  */
  @Test
  fun testFall() {
    assertSame(SATURN, impl.getPointOld(ARIES, FALL))
    assertSame(null, impl.getPointOld(TAURUS, FALL))
    //assertSame(LunarNode.SOUTH   , impl.getPoint(ZodiacSign.GEMINI      , Dignity.FALL));
    assertSame(MARS, impl.getPointOld(CANCER, FALL))
    assertSame(null, impl.getPointOld(LEO, FALL))
    assertSame(VENUS, impl.getPointOld(VIRGO, FALL))
    assertSame(SUN, impl.getPointOld(LIBRA, FALL))
    assertSame(MOON, impl.getPointOld(SCORPIO, FALL))
    //assertSame(LunarNode.NORTH   , impl.getPoint(ZodiacSign.SAGITTARIUS , Dignity.FALL));
    assertSame(JUPITER, impl.getPointOld(CAPRICORN, FALL))
    assertSame(null, impl.getPointOld(AQUARIUS, FALL))
    assertSame(MERCURY, impl.getPointOld(PISCES, FALL))
  }



}
