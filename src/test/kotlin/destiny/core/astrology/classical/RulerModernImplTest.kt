/**
 * Created by smallufo on 2026-11-14.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RulerModernImplTest {

  @Test
  fun `天蠍 水瓶 雙魚是三王星 其餘與托勒密相同`() = with(RulerModernImpl) {
    assertEquals(Planet.PLUTO, ZodiacSign.SCORPIO.getRulerPoint())
    assertEquals(Planet.URANUS, ZodiacSign.AQUARIUS.getRulerPoint())
    assertEquals(Planet.NEPTUNE, ZodiacSign.PISCES.getRulerPoint())
    // 不分日夜
    assertEquals(Planet.PLUTO, ZodiacSign.SCORPIO.getRulerPoint(DayNight.DAY))
    assertEquals(Planet.PLUTO, ZodiacSign.SCORPIO.getRulerPoint(DayNight.NIGHT))
    (ZodiacSign.entries - modernRulers.keys).forEach { sign ->
      assertEquals(with(RulerPtolemyImpl) { sign.getRulerPoint() }, sign.getRulerPoint(), sign.name)
    }
  }

  @Test
  fun `每個星座恰有一顆守護星 每顆星守護的星座反查得回來`() = with(RulerModernImpl) {
    val bySign = ZodiacSign.entries.associateWith { it.getRulerPoint()!! }
    assertEquals(10, bySign.values.toSet().size)
    bySign.values.toSet().forEach { p ->
      assertEquals(bySign.filterValues { it === p }.keys, p.getRulingSigns(), p.toString())
    }
    assertEquals(setOf(ZodiacSign.ARIES), Planet.MARS.getRulingSigns())
    assertEquals(setOf(ZodiacSign.CAPRICORN), Planet.SATURN.getRulingSigns())
    assertEquals(setOf(ZodiacSign.SAGITTARIUS), Planet.JUPITER.getRulingSigns())
    assertEquals(setOf(ZodiacSign.GEMINI, ZodiacSign.VIRGO), Planet.MERCURY.getRulingSigns())
  }

  @Test
  fun `日夜版本 不再回已經讓給三王星的星座`() = with(RulerModernImpl) {
    assertEquals(ZodiacSign.SCORPIO, Planet.PLUTO.getRulingSign(DayNight.DAY))
    DayNight.entries.forEach { dn ->
      listOf(Planet.MARS, Planet.SATURN, Planet.JUPITER).forEach { p ->
        val sign = p.getRulingSign(dn)
        assert(sign == null || sign !in modernRulers) { "$p $dn → $sign" }
      }
    }
    assertNull(Planet.MARS.getRulingSign(DayNight.NIGHT).takeIf { it == ZodiacSign.SCORPIO })
  }
}
