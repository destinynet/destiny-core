/**
 * Created by smallufo on 2026-11-26.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecennialsTest {

  private val birth = GmtJulDay(2_450_000.0)

  /** 月 10°、土 40°、日 100°、水 110°、金 150°、火 200°、木 350° */
  private val positions = mapOf(
    MOON to 10.0, SATURN to 40.0, SUN to 100.0, MERCURY to 110.0, VENUS to 150.0, MARS to 200.0, JUPITER to 350.0,
  ).mapValues { it.value.toZodiacDegree() }

  private fun Decennial.days() = toTime - fromTime

  @Test
  fun `最小年總和 129，黃道釋放的星座年數沿用主星最小年（摩羯除外）`() {
    assertEquals(129, PLANET_LEAST_YEARS_TOTAL)
    assertEquals(DECENNIAL_MAJOR_MONTHS, PLANET_LEAST_YEARS_TOTAL)
    assertEquals(25, zodiacalReleasingYears[ZodiacSign.CANCER])
    assertEquals(19, zodiacalReleasingYears[ZodiacSign.LEO])
    assertEquals(30, zodiacalReleasingYears[ZodiacSign.AQUARIUS])
    assertEquals(27, zodiacalReleasingYears[ZodiacSign.CAPRICORN])
    assertEquals(211, ZODIACAL_RELEASING_TOTAL_YEARS)
  }

  @Test
  fun `順序：從起始星順黃道往後排、繞過 0 度`() {
    assertEquals(listOf(SUN, MERCURY, VENUS, MARS, JUPITER, MOON, SATURN), decennialsOrder(SUN, positions))
    assertEquals(listOf(MOON, SATURN, SUN, MERCURY, VENUS, MARS, JUPITER), decennialsOrder(MOON, positions))
  }

  @Test
  fun `起始星是區分光體`() {
    assertEquals(SUN, DecennialsSectLight.starter(true, positions))
    assertEquals(MOON, DecennialsSectLight.starter(false, positions))
  }

  @Test
  fun `主限各 129 個月、七星輪替後重新開始`() {
    val order = decennialsOrder(SUN, positions)
    val majors = generateDecennials(order, birth, birth, birth + 80 * 365.25, maxLevel = 1)
    assertEquals(order + order.take(1), majors.map { it.lord })
    majors.forEach { assertEquals(129 * 30.0, it.days(), 1e-9) }
    assertEquals(birth + 7 * 129 * 30.0, majors[7].fromTime)
  }

  @Test
  fun `子限：從主限主星起，每顆星分得最小年個月`() {
    val order = decennialsOrder(SUN, positions)
    // 第二個主限（水星）
    val all = generateDecennials(order, birth, birth + 129 * 30.0, birth + 2 * 129 * 30.0 - 1, maxLevel = 2)
    val subs = all.filter { it.level == 2 }
    assertEquals(listOf(MERCURY, VENUS, MARS, JUPITER, MOON, SATURN, SUN), subs.map { it.lord })
    subs.forEach {
      assertEquals(MERCURY, it.majorLord)
      assertEquals(planetLeastYears.getValue(it.lord) * 30.0, it.days(), 1e-6)
    }
    assertEquals(all.first { it.level == 1 }.toTime, subs.last().toTime)
  }

  @Test
  fun `第三層：子限依同一比例再分`() {
    val order = decennialsOrder(SUN, positions)
    val l3 = generateDecennials(order, birth, birth, birth + 1, maxLevel = 3).filter { it.level == 3 }
    // 第一個子限是太陽 19 個月 = 570 天，太陽分得 570 × 19 / 129
    assertEquals(listOf(SUN, SUN), l3.first().lords.drop(1))
    assertEquals(570.0 * 19 / 129, l3.first().days(), 1e-9)
  }

  @Test
  fun `某一時刻的各層`() {
    val order = decennialsOrder(MOON, positions)
    // 出生後 129 + 25 + 1 個月：第二主限（土星）、第二個子限（日，土星 30 個月之後）
    val t = birth + (129 + 31) * 30.0
    val at = decennialsAt(order, birth, t, maxLevel = 3)
    assertEquals(listOf(1, 2, 3), at.map { it.level })
    assertEquals(listOf(SATURN, SUN), at[1].lords)
    assertEquals(at[1].lords, at[2].lords.dropLast(1))
  }

  @Test
  fun `365_25 天一年時各層等比放大、順序不變`() {
    val order = decennialsOrder(SUN, positions)
    // 兩者的窗都恰好是第一個主限
    val valens = generateDecennials(order, birth, birth, birth + 129 * 30.0, maxLevel = 2)
    val julian = generateDecennials(order, birth, birth, birth + 129 * 365.25 / 12, maxLevel = 2, yearLength = ValensYearLength.JULIAN_365_25)
    assertEquals(129 * 365.25 / 12, julian.first().days(), 1e-9)
    assertEquals(8, valens.size)
    assertEquals(valens.map { it.lords }, julian.map { it.lords })
  }

  @Test
  fun `順序必須是七星`() {
    assertFailsWith<IllegalArgumentException> { generateDecennials(listOf(SUN, MOON), birth, birth, birth + 1) }
  }
}
