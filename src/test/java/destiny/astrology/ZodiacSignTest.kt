/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:43:25
 */
package destiny.astrology

import destiny.astrology.ZodiacSign.*
import destiny.core.chinese.Branch
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ZodiacSignTest {

  @Test
  fun testIndex() {
    assertSame(PISCES, ZodiacSign.get(-1))
    assertSame(ARIES , ZodiacSign.get(0))
    assertSame(PISCES, ZodiacSign.get(11))
    assertSame(ARIES, ZodiacSign.get(12))
  }

  @Test
  fun testLoop() {
    assertSame(PISCES, ARIES.previous)
    assertSame(PISCES, ARIES.prev(1))
    assertSame(PISCES, ARIES.prev(13))
    assertSame(PISCES, ARIES.next(-1))

    assertSame(TAURUS , ARIES.next)
    assertSame(TAURUS , ARIES.next(1))
    assertSame(TAURUS , ARIES.next(13))
    assertSame(TAURUS , ARIES.prev(-1))
  }

  @Test
  fun testGetZodiacSign() {
    assertSame(ARIES, ZodiacSign.of(0.0))
    assertSame(ARIES, ZodiacSign.of(29.99))
    assertSame(TAURUS, ZodiacSign.of(30.0))
    assertSame(TAURUS, ZodiacSign.of(59.99))
    assertSame(GEMINI, ZodiacSign.of(60.0))
    assertSame(CANCER, ZodiacSign.of(90.0))
    assertSame(LEO, ZodiacSign.of(120.0))
    assertSame(VIRGO, ZodiacSign.of(150.0))
    assertSame(LIBRA, ZodiacSign.of(180.0))
    assertSame(SCORPIO, ZodiacSign.of(210.0))
    assertSame(SAGITTARIUS, ZodiacSign.of(240.0))
    assertSame(CAPRICORN, ZodiacSign.of(270.0))
    assertSame(AQUARIUS, ZodiacSign.of(300.0))
    assertSame(PISCES, ZodiacSign.of(330.0))
    //度數大於 360
    assertSame(ARIES, ZodiacSign.of(360.0))
    assertSame(TAURUS, ZodiacSign.of(390.0))
    assertSame(ARIES, ZodiacSign.of(720.0))
    assertSame(TAURUS, ZodiacSign.of(750.0))
    //度數小於零
    assertSame(PISCES, ZodiacSign.of(-1.0))
    assertSame(PISCES, ZodiacSign.of(-30.0))
    assertSame(AQUARIUS, ZodiacSign.of(-31.0))
    assertSame(AQUARIUS, ZodiacSign.of(-60.0))
    assertSame(CAPRICORN, ZodiacSign.of(-61.0))
    assertSame(CAPRICORN, ZodiacSign.of(-90.0))
    assertSame(TAURUS, ZodiacSign.of(-301.0))
    assertSame(TAURUS, ZodiacSign.of(-330.0))
    assertSame(ARIES, ZodiacSign.of(-331.0))
    assertSame(ARIES, ZodiacSign.of(-360.0))
    assertSame(PISCES, ZodiacSign.of(-361.0))
    assertSame(PISCES, ZodiacSign.of(-390.0))
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
  fun testGetBranch() {
    assertSame(Branch.戌 , ARIES.branch)
    assertSame(Branch.酉 , TAURUS.branch)
    assertSame(Branch.申 , GEMINI.branch)
    assertSame(Branch.未 , CANCER.branch)
    assertSame(Branch.午 , LEO.branch)
    assertSame(Branch.巳 , VIRGO.branch)
    assertSame(Branch.辰 , LIBRA.branch)
    assertSame(Branch.卯 , SCORPIO.branch)
    assertSame(Branch.寅 , SAGITTARIUS.branch)
    assertSame(Branch.丑 , CAPRICORN.branch)
    assertSame(Branch.子 , AQUARIUS.branch)
    assertSame(Branch.亥 , PISCES.branch)
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
