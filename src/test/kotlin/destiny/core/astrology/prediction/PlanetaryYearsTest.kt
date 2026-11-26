/**
 * Created by smallufo on 2026-11-26.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.classical.AbstractPtolemy
import kotlin.test.Test
import kotlin.test.assertEquals

class PlanetaryYearsTest {

  @Test
  fun `七星最小年，總和 129`() {
    assertEquals(Planet.classicalSet, planetLeastYears.keys)
    assertEquals(
      mapOf(Planet.SATURN to 30, Planet.JUPITER to 12, Planet.MARS to 15, Planet.SUN to 19, Planet.VENUS to 8, Planet.MERCURY to 20, Planet.MOON to 25),
      planetLeastYears
    )
    assertEquals(129, PLANET_LEAST_YEARS_TOTAL)
    assertEquals(PLANET_LEAST_YEARS_TOTAL, DECENNIAL_MAJOR_MONTHS)
  }

  /** 兩張表的關係：星座年數 = 主星最小年，只有摩羯是 27（土星 30） */
  @Test
  fun `星座年數除摩羯外等於主星最小年`() {
    val differing = ZodiacSign.entries.filter { sign ->
      zodiacalReleasingYears.getValue(sign) != planetLeastYears.getValue(AbstractPtolemy.rulerMap.getValue(sign))
    }
    assertEquals(listOf(ZodiacSign.CAPRICORN), differing)
    assertEquals(27, zodiacalReleasingYears.getValue(ZodiacSign.CAPRICORN))
    assertEquals(211, ZODIACAL_RELEASING_TOTAL_YEARS)
  }

  @Test
  fun `年長：月是年的十二分之一，ZrYearLength 是同一個型別`() {
    assertEquals(EGYPTIAN_MONTH_DAYS, ValensYearLength.VALENS_360.monthDays)
    assertEquals(365.25 / 12, ValensYearLength.JULIAN_365_25.monthDays)
    assertEquals(ValensYearLength.VALENS_360, ZrYearLength.DEFAULT)
    assertEquals(ValensYearLength.entries, ZrYearLength.entries)
  }
}
