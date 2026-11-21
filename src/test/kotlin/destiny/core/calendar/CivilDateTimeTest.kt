/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import kotlinx.datetime.LocalDateTime
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.JulianFields
import kotlin.test.*

class CivilDateTimeTest {

  /**
   * 逐日對照：儒略日 0（儒略曆 BC 4713-01-01）到格里曆 3000-12-31，約 280 萬天。
   * 格里曆以 `java.time.LocalDate`、儒略曆以 threeten-extra `JulianDate` 為標準答案（兩者皆 proleptic）。
   */
  @Test
  fun `逐日對照 java time 與 threeten-extra`() {
    val first = 0L
    val last = LocalDate.of(3000, 12, 31).getLong(JulianFields.JULIAN_DAY)
    for (jdn in first..last) {
      val g = LocalDate.MIN.with(JulianFields.JULIAN_DAY, jdn)
      val j = JulianDate.from(g)

      val (gy, gm, gd) = CivilDateTime.fromJdn(CalType.GREGORIAN, jdn)
      if (gy != g.year || gm != g.monthValue || gd != g.dayOfMonth) fail("格里曆 JDN $jdn：期望 $g，得到 $gy-$gm-$gd")
      val (jy, jm, jd) = CivilDateTime.fromJdn(CalType.JULIAN, jdn)
      val jYear = j.get(ChronoField.YEAR)
      if (jy != jYear || jm != j.get(ChronoField.MONTH_OF_YEAR) || jd != j.get(ChronoField.DAY_OF_MONTH)) fail("儒略曆 JDN $jdn：期望 $j，得到 $jy-$jm-$jd")

      if (CivilDateTime.toJdn(CalType.GREGORIAN, gy, gm, gd) != jdn) fail("格里曆 $g → JDN 不符")
      if (CivilDateTime.toJdn(CalType.JULIAN, jy, jm, jd) != jdn) fail("儒略曆 $j → JDN 不符")
    }
  }

  @Test
  fun `儒略曆 1700-02-29 合法、格里曆不合法`() {
    val j = CivilDateTime(CalType.JULIAN, 1700, 2, 29, 8, 30)
    assertEquals(LocalDateTime(1700, 3, 11, 8, 30), j.toGregorian())
    assertFailsWith<IllegalArgumentException> { CivilDateTime(CalType.GREGORIAN, 1700, 2, 29) }
  }

  @Test
  fun `改曆：儒略曆 1582-10-04 的隔天是格里曆 1582-10-15`() {
    val lastJulian = CivilDateTime(CalType.JULIAN, 1582, 10, 4)
    val firstGregorian = CivilDateTime(CalType.GREGORIAN, 1582, 10, 15)
    assertEquals(lastJulian.julianDayNumber() + 1, firstGregorian.julianDayNumber())
    assertEquals(CivilDateTime(CalType.JULIAN, 1582, 10, 5), firstGregorian.withCalendar(CalType.JULIAN))
  }

  @Test
  fun `與既有的 JulianDateTime 換算一致（凱撒遇刺：儒略曆 BC 44-03-15）`() {
    val civil = CivilDateTime(CalType.JULIAN, -43, 3, 15, 12, 0)
    val legacy = JulianDateTime.of(-43, 3, 15, 12, 0, 0, 0).toGregorianLocalDateTime()
    assertEquals(LocalDateTime(legacy.year, legacy.monthValue, legacy.dayOfMonth, legacy.hour, legacy.minute), civil.toGregorian())
  }

  @Test
  fun `往返：格里曆 → 儒略曆 → 格里曆`() {
    val g = LocalDateTime(1918, 2, 14, 6, 45, 30, 123_000_000)
    val j = CivilDateTime.of(g, CalType.JULIAN)
    assertEquals(CivilDateTime(CalType.JULIAN, 1918, 2, 1, 6, 45, 30, 123_000_000), j)   // 俄國改曆：1918-02-01 儒略 ＝ 02-14 格里
    assertEquals(g, j.toGregorian())
  }

  @Test
  fun `localJulianDay：午夜為半日`() {
    assertEquals(2451544.5, CivilDateTime(CalType.GREGORIAN, 2000, 1, 1).localJulianDay())
    assertEquals(2451545.0, CivilDateTime(CalType.GREGORIAN, 2000, 1, 1, 12).localJulianDay())
  }

  @Test
  fun `不同曆法依時刻比較`() {
    val j = CivilDateTime(CalType.JULIAN, 1700, 2, 29)   // ＝ 格里曆 1700-03-11
    assertTrue(j > CivilDateTime(CalType.GREGORIAN, 1700, 3, 10))
    assertTrue(j < CivilDateTime(CalType.GREGORIAN, 1700, 3, 12))
    assertEquals(0, j.compareTo(CivilDateTime(CalType.GREGORIAN, 1700, 3, 11)))
  }

  @Test
  fun `toString 帶曆法代號`() {
    assertEquals("J 1700-02-29 08:30:00", CivilDateTime(CalType.JULIAN, 1700, 2, 29, 8, 30).toString())
    assertEquals("G -0043-03-13 12:00:00", CivilDateTime(CalType.GREGORIAN, -43, 3, 13, 12).toString())
  }

  @Test
  fun `超出支援範圍`() {
    assertFailsWith<IllegalArgumentException> { CivilDateTime(CalType.JULIAN, -4713, 12, 31) }
    assertEquals(0L, CivilDateTime(CalType.JULIAN, -4712, 1, 1).julianDayNumber())
  }
}
