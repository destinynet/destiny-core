/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:43:25
 */
package destiny.astrology

import destiny.astrology.ZodiacSign.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ZodiacSignTest {

  @Test
  fun testGetZodiacSign() {
    assertSame(ARIES, ZodiacSign.getZodiacSign(0.0))
    assertSame(ARIES, ZodiacSign.getZodiacSign(29.99))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(30.0))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(59.99))
    assertSame(GEMINI, ZodiacSign.getZodiacSign(60.0))
    assertSame(CANCER, ZodiacSign.getZodiacSign(90.0))
    assertSame(LEO, ZodiacSign.getZodiacSign(120.0))
    assertSame(VIRGO, ZodiacSign.getZodiacSign(150.0))
    assertSame(LIBRA, ZodiacSign.getZodiacSign(180.0))
    assertSame(SCORPIO, ZodiacSign.getZodiacSign(210.0))
    assertSame(SAGITTARIUS, ZodiacSign.getZodiacSign(240.0))
    assertSame(CAPRICORN, ZodiacSign.getZodiacSign(270.0))
    assertSame(AQUARIUS, ZodiacSign.getZodiacSign(300.0))
    assertSame(PISCES, ZodiacSign.getZodiacSign(330.0))
    //度數大於 360
    assertSame(ARIES, ZodiacSign.getZodiacSign(360.0))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(390.0))
    assertSame(ARIES, ZodiacSign.getZodiacSign(720.0))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(750.0))
    //度數小於零
    assertSame(PISCES, ZodiacSign.getZodiacSign(-1.0))
    assertSame(PISCES, ZodiacSign.getZodiacSign(-30.0))
    assertSame(AQUARIUS, ZodiacSign.getZodiacSign(-31.0))
    assertSame(AQUARIUS, ZodiacSign.getZodiacSign(-60.0))
    assertSame(CAPRICORN, ZodiacSign.getZodiacSign(-61.0))
    assertSame(CAPRICORN, ZodiacSign.getZodiacSign(-90.0))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(-301.0))
    assertSame(TAURUS, ZodiacSign.getZodiacSign(-330.0))
    assertSame(ARIES, ZodiacSign.getZodiacSign(-331.0))
    assertSame(ARIES, ZodiacSign.getZodiacSign(-360.0))
    assertSame(PISCES, ZodiacSign.getZodiacSign(-361.0))
    assertSame(PISCES, ZodiacSign.getZodiacSign(-390.0))
  }

  @Test
  fun testGetIndex() {
    assertSame(0, ARIES.index)
    assertSame(1, TAURUS.index)
    assertSame(2, GEMINI.index)
    assertSame(3, CANCER.index)
    assertSame(4, LEO.index)
    assertSame(5, VIRGO.index)
    assertSame(6, LIBRA.index)
    assertSame(7, SCORPIO.index)
    assertSame(8, SAGITTARIUS.index)
    assertSame(9, CAPRICORN.index)
    assertSame(10, AQUARIUS.index)
    assertSame(11, PISCES.index)
  }

  @Test
  fun testGetOppositeSign() {
    assertSame(LIBRA, ARIES.oppositeSign)
    assertSame(SCORPIO, TAURUS.oppositeSign)
    assertSame(SAGITTARIUS, GEMINI.oppositeSign)
    assertSame(CAPRICORN, CANCER.oppositeSign)
    assertSame(AQUARIUS, LEO.oppositeSign)
    assertSame(PISCES, VIRGO.oppositeSign)
    assertSame(ARIES, LIBRA.oppositeSign)
    assertSame(TAURUS, SCORPIO.oppositeSign)
    assertSame(GEMINI, SAGITTARIUS.oppositeSign)
    assertSame(CANCER, CAPRICORN.oppositeSign)
    assertSame(LEO, AQUARIUS.oppositeSign)
    assertSame(VIRGO, PISCES.oppositeSign)
  }

  @Test
  fun testToString() {
    assertEquals("牡羊", ARIES.toString())
    assertEquals("金牛", TAURUS.toString())
    assertEquals("雙子", GEMINI.toString())
    assertEquals("巨蟹", CANCER.toString())
    assertEquals("獅子", LEO.toString())
    assertEquals("處女", VIRGO.toString())
    assertEquals("天秤", LIBRA.toString())
    assertEquals("天蠍", SCORPIO.toString())
    assertEquals("射手", SAGITTARIUS.toString())
    assertEquals("摩羯", CAPRICORN.toString())
    assertEquals("水瓶", AQUARIUS.toString())
    assertEquals("雙魚", PISCES.toString())
  }

  @Test
  fun testToStringLocale() {
    assertEquals("Aries", ARIES.toString(Locale.US))
    assertEquals("Taurus", TAURUS.toString(Locale.US))
    assertEquals("Gemini", GEMINI.toString(Locale.US))
    assertEquals("Cancer", CANCER.toString(Locale.US))
    assertEquals("Leo", LEO.toString(Locale.US))
    assertEquals("Virgo", VIRGO.toString(Locale.US))
    assertEquals("Libra", LIBRA.toString(Locale.US))
    assertEquals("Scorpio", SCORPIO.toString(Locale.US))
    assertEquals("Sagittarius", SAGITTARIUS.toString(Locale.US))
    assertEquals("Capricorn", CAPRICORN.toString(Locale.US))
    assertEquals("Aquarius", AQUARIUS.toString(Locale.US))
    assertEquals("Pisces", PISCES.toString(Locale.US))
  }

  @Test
  fun testGetAbbreviation() {
    assertEquals("牡", ARIES.abbreviation)
    assertEquals("牛", TAURUS.abbreviation)
    assertEquals("孖", GEMINI.abbreviation)
    assertEquals("蟹", CANCER.abbreviation)
    assertEquals("獅", LEO.abbreviation)
    assertEquals("處", VIRGO.abbreviation)
    assertEquals("秤", LIBRA.abbreviation)
    assertEquals("蠍", SCORPIO.abbreviation)
    assertEquals("射", SAGITTARIUS.abbreviation)
    assertEquals("羯", CAPRICORN.abbreviation)
    assertEquals("瓶", AQUARIUS.abbreviation)
    assertEquals("魚", PISCES.abbreviation)
  }

  @Test
  fun testGetAbbreviationLocale() {
    assertEquals("Ari", ARIES.getAbbreviation(Locale.US))
    assertEquals("Tau", TAURUS.getAbbreviation(Locale.US))
    assertEquals("Gem", GEMINI.getAbbreviation(Locale.US))
    assertEquals("Can", CANCER.getAbbreviation(Locale.US))
    assertEquals("Leo", LEO.getAbbreviation(Locale.US))
    assertEquals("Vir", VIRGO.getAbbreviation(Locale.US))
    assertEquals("Lib", LIBRA.getAbbreviation(Locale.US))
    assertEquals("Sco", SCORPIO.getAbbreviation(Locale.US))
    assertEquals("Sag", SAGITTARIUS.getAbbreviation(Locale.US))
    assertEquals("Cap", CAPRICORN.getAbbreviation(Locale.US))
    assertEquals("Aqu", AQUARIUS.getAbbreviation(Locale.US))
    assertEquals("Pis", PISCES.getAbbreviation(Locale.US))
  }
}
