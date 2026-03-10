/**
 * Test data sourced from astro-seek.com:
 * Birth: 1970-01-01 00:00:00 CST (Taiwan, GMT+8) = 1969-12-31 16:00:00 UTC
 * Location: 25°3'N, 121°32'E (Taipei)
 * Lot of Fortune in Capricorn
 *
 * Created by smallufo on 2026-03-11.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet.SATURN
import destiny.core.astrology.ZodiacSign.*
import destiny.core.calendar.GmtJulDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ZodiacalReleasingTest {

  // 1969-12-31 16:00:00 UTC (= 1970-01-01 00:00:00 CST)
  // Julian Day for 1970-01-01 00:00:00 UTC = 2440587.5
  // But our birth is 1969-12-31 16:00:00 UTC = 2440587.5 - 8/24 = 2440587.1667
  private val birthTime = GmtJulDay(2440587.5 - 8.0 / 24.0)

  private val lotSign = CAPRICORN

  /** Convert a GmtJulDay to LocalDateTime in UTC for readable assertions */
  private fun GmtJulDay.toUtcLocalDateTime(): LocalDateTime {
    // JD 2440587.5 = 1970-01-01 00:00:00 UTC
    val epochJd = 2440587.5
    val daysSinceEpoch = this.value - epochJd
    val secondsSinceEpoch = (daysSinceEpoch * 86400).toLong()
    return LocalDateTime.ofEpochSecond(secondsSinceEpoch, 0, ZoneOffset.UTC)
  }

  /** Convert UTC LocalDateTime to Taiwan local time (UTC+8) */
  private fun LocalDateTime.toTaiwan(): LocalDateTime = this.plusHours(8)

  /** Assert that the date part (in Taiwan time) matches expected */
  private fun assertTaiwanDate(expected: LocalDate, actual: GmtJulDay, tolerance: Long = 1) {
    val taiwanDate = actual.toUtcLocalDateTime().toTaiwan().toLocalDate()
    val daysDiff = ChronoUnit.DAYS.between(expected, taiwanDate)
    assertTrue { kotlin.math.abs(daysDiff) <= tolerance }
  }

  /** Assert that the datetime (in Taiwan time) matches expected, with hour-level tolerance */
  private fun assertTaiwanDateTime(expected: LocalDateTime, actual: GmtJulDay, toleranceHours: Long = 1) {
    val taiwanDateTime = actual.toUtcLocalDateTime().toTaiwan()
    val hoursDiff = ChronoUnit.HOURS.between(expected, taiwanDateTime)
    assertTrue { kotlin.math.abs(hoursDiff) <= toleranceHours }
  }

  @Test
  fun testSignYearsSum() {
    assertEquals(211, ZODIACAL_RELEASING_TOTAL_YEARS)
  }

  @Test
  fun testLevelUnitDays() {
    assertEquals(360.0, levelUnitDays(1), 0.001)  // L1: years
    assertEquals(30.0, levelUnitDays(2), 0.001)   // L2: months
    assertEquals(2.5, levelUnitDays(3), 0.001)    // L3: ~days
    assertEquals(5.0 / 24.0, levelUnitDays(4), 0.0001) // L4: ~hours
  }

  @Test
  fun `L1 periods from Capricorn`() {
    val endTime = GmtJulDay(birthTime.value + 100 * 365.25) // ~100 years
    val l1 = generateL1(lotSign, birthTime, endTime)

    // L1 Capricorn: 27 Egyptian years = 9720 days
    assertEquals(CAPRICORN, l1[0].sign)
    assertEquals(SATURN, l1[0].lord)
    assertEquals(1, l1[0].level)
    assertEquals(9720.0, l1[0].toTime.value - l1[0].fromTime.value, 0.001)

    // L1 Capricorn: 1970-01-01 to ~1996-08-12 (Taiwan time)
    assertTaiwanDate(LocalDate.of(1970, 1, 1), l1[0].fromTime)
    assertTaiwanDate(LocalDate.of(1996, 8, 12), l1[0].toTime, 2)

    // L1 Aquarius: 30 Egyptian years = 10800 days
    assertEquals(AQUARIUS, l1[1].sign)
    assertEquals(SATURN, l1[1].lord)
    assertEquals(10800.0, l1[1].toTime.value - l1[1].fromTime.value, 0.001)

    // L1 Aquarius: ~1996-08-12 to ~2026-03-08
    assertTaiwanDate(LocalDate.of(2026, 3, 8), l1[1].toTime, 2)
  }

  @Test
  fun `L2 periods within L1 Capricorn`() {
    // L1 apricorn: 27 years × 360 days = 9720 days
    val l1Duration = 27.0 * EGYPTIAN_YEAR_DAYS
    val l2 = generateSubPeriods(CAPRICORN, birthTime, l1Duration, 2)

    // First 12 signs (normal pass) + LB signs
    // Total L2 first pass: 211 months × 30 days = 6330 days
    // Remaining: 9720 - 6330 = 3390 days → LB from Cancer

    // Verify first few L2 periods
    // L2 Cap: 27 months = 810 days, starts 1970-01-01
    assertEquals(CAPRICORN, l2[0].sign)
    assertEquals(SATURN, l2[0].lord)
    assertEquals(810.0, l2[0].toTime.value - l2[0].fromTime.value, 0.001)
    assertFalse(l2[0].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1970, 1, 1), l2[0].fromTime)

    // L2 Aqu: 30 months = 900 days, starts ~1972-03-21
    assertEquals(AQUARIUS, l2[1].sign)
    assertEquals(900.0, l2[1].toTime.value - l2[1].fromTime.value, 0.001)
    assertTaiwanDate(LocalDate.of(1972, 3, 21), l2[1].fromTime, 2)

    // L2 Pis: 12 months = 360 days, starts ~1974-09-07
    assertEquals(PISCES, l2[2].sign)
    assertEquals(360.0, l2[2].toTime.value - l2[2].fromTime.value, 0.001)
    assertTaiwanDate(LocalDate.of(1974, 9, 7), l2[2].fromTime, 2)

    // L2 Ari: starts ~1975-09-02
    assertEquals(ARIES, l2[3].sign)
    assertTaiwanDate(LocalDate.of(1975, 9, 2), l2[3].fromTime, 2)

    // L2 Tau: starts ~1976-11-25
    assertEquals(TAURUS, l2[4].sign)
    assertTaiwanDate(LocalDate.of(1976, 11, 25), l2[4].fromTime, 2)

    // L2 Gem: starts ~1977-07-23
    assertEquals(GEMINI, l2[5].sign)
    assertTaiwanDate(LocalDate.of(1977, 7, 23), l2[5].fromTime, 2)

    // L2 Can: starts ~1979-03-15 (Pre LB marker in astro-seek)
    assertEquals(CANCER, l2[6].sign)
    assertTaiwanDate(LocalDate.of(1979, 3, 15), l2[6].fromTime, 2)
    assertFalse(l2[6].isLoosingOfBond) // first pass, not yet LB

    // L2 Leo: starts ~1981-04-03
    assertEquals(LEO, l2[7].sign)
    assertTaiwanDate(LocalDate.of(1981, 4, 3), l2[7].fromTime, 2)

    // L2 Vir: starts ~1982-10-25
    assertEquals(VIRGO, l2[8].sign)
    assertTaiwanDate(LocalDate.of(1982, 10, 25), l2[8].fromTime, 2)

    // L2 Lib: starts ~1984-06-16 (Culmination)
    assertEquals(LIBRA, l2[9].sign)
    assertTaiwanDate(LocalDate.of(1984, 6, 16), l2[9].fromTime, 2)

    // L2 Sco: starts ~1985-02-11
    assertEquals(SCORPIO, l2[10].sign)
    assertTaiwanDate(LocalDate.of(1985, 2, 11), l2[10].fromTime, 2)

    // L2 Sag: starts ~1986-05-07 (last of first pass)
    assertEquals(SAGITTARIUS, l2[11].sign)
    assertTaiwanDate(LocalDate.of(1986, 5, 7), l2[11].fromTime, 2)

    // === Loosing of the Bond ===
    // L2 Can (LB): starts ~1987-05-02
    assertEquals(CANCER, l2[12].sign)
    assertTrue(l2[12].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1987, 5, 2), l2[12].fromTime, 2)

    // L2 Leo (LB): starts ~1989-05-21
    assertEquals(LEO, l2[13].sign)
    assertTrue(l2[13].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1989, 5, 21), l2[13].fromTime, 2)

    // L2 Vir (LB): starts ~1990-12-12
    assertEquals(VIRGO, l2[14].sign)
    assertTrue(l2[14].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1990, 12, 12), l2[14].fromTime, 2)

    // L2 Lib (LB): starts ~1992-08-03 (Culmination)
    assertEquals(LIBRA, l2[15].sign)
    assertTrue(l2[15].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1992, 8, 3), l2[15].fromTime, 2)

    // L2 Sco (LB): starts ~1993-03-31
    assertEquals(SCORPIO, l2[16].sign)
    assertTrue(l2[16].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1993, 3, 31), l2[16].fromTime, 2)

    // L2 Sag (LB): starts ~1994-06-24
    assertEquals(SAGITTARIUS, l2[17].sign)
    assertTrue(l2[17].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1994, 6, 24), l2[17].fromTime, 2)

    // L2 Cap (LB, Completion): starts ~1995-06-19
    assertEquals(CAPRICORN, l2[18].sign)
    assertTrue(l2[18].isLoosingOfBond)
    assertTaiwanDate(LocalDate.of(1995, 6, 19), l2[18].fromTime, 2)

    // Total: 12 normal + 7 LB = 19 sub-periods
    assertEquals(19, l2.size)
  }

  @Test
  fun `L2 periods within L1 Aquarius`() {
    // L1 Aquarius starts after L1 Capricorn
    val l1AquStart = GmtJulDay(birthTime.value + 27.0 * EGYPTIAN_YEAR_DAYS)
    val l1AquDuration = 30.0 * EGYPTIAN_YEAR_DAYS // 10800 days
    val l2 = generateSubPeriods(AQUARIUS, l1AquStart, l1AquDuration, 2)

    // L2 Aqu: starts ~1996-08-12
    assertEquals(AQUARIUS, l2[0].sign)
    assertTaiwanDate(LocalDate.of(1996, 8, 12), l2[0].fromTime, 2)

    // L2 Pis: starts ~1999-01-29
    assertEquals(PISCES, l2[1].sign)
    assertTaiwanDate(LocalDate.of(1999, 1, 29), l2[1].fromTime, 2)

    // L2 Ari: starts ~2000-01-24
    assertEquals(ARIES, l2[2].sign)
    assertTaiwanDate(LocalDate.of(2000, 1, 24), l2[2].fromTime, 2)

    // L2 Tau: starts ~2001-04-18
    assertEquals(TAURUS, l2[3].sign)
    assertTaiwanDate(LocalDate.of(2001, 4, 18), l2[3].fromTime, 2)
  }

  @Test
  fun `L3 periods within L2 Taurus under L1 Aquarius`() {
    // L2 Taurus starts at L1_Aqu_start + (Aqu=900 + Pis=360 + Ari=450) days
    val l1AquStart = GmtJulDay(birthTime.value + 27.0 * EGYPTIAN_YEAR_DAYS)
    val l2TauStart = GmtJulDay(l1AquStart.value + (30 + 12 + 15) * EGYPTIAN_MONTH_DAYS)
    val l2TauDuration = 8.0 * EGYPTIAN_MONTH_DAYS // 240 days

    val l3 = generateSubPeriods(TAURUS, l2TauStart, l2TauDuration, 3)

    val l3Unit = levelUnitDays(3) // 2.5 days per year

    // L3 Tau: 8 × 2.5 = 20 days, starts ~2001-04-18
    assertEquals(TAURUS, l3[0].sign)
    assertEquals(20.0, l3[0].toTime.value - l3[0].fromTime.value, 0.01)
    assertTaiwanDate(LocalDate.of(2001, 4, 18), l3[0].fromTime, 2)

    // L3 Gem: 20 × 2.5 = 50 days, starts ~2001-05-08
    assertEquals(GEMINI, l3[1].sign)
    assertEquals(50.0, l3[1].toTime.value - l3[1].fromTime.value, 0.01)
    assertTaiwanDate(LocalDate.of(2001, 5, 8), l3[1].fromTime, 2)

    // L3 Can: 25 × 2.5 = 62.5 days, starts ~2001-06-27
    assertEquals(CANCER, l3[2].sign)
    assertEquals(62.5, l3[2].toTime.value - l3[2].fromTime.value, 0.01)
    assertTaiwanDate(LocalDate.of(2001, 6, 27), l3[2].fromTime, 2)

    // L3 Leo: 19 × 2.5 = 47.5 days, starts ~2001-08-28 12:00
    assertEquals(LEO, l3[3].sign)
    assertEquals(47.5, l3[3].toTime.value - l3[3].fromTime.value, 0.01)

    // L3 Vir: 20 × 2.5 = 50 days, starts ~2001-10-15
    assertEquals(VIRGO, l3[4].sign)
    assertEquals(50.0, l3[4].toTime.value - l3[4].fromTime.value, 0.01)
    assertTaiwanDate(LocalDate.of(2001, 10, 15), l3[4].fromTime, 2)

    // L3 Lib: starts ~2001-12-04, but truncated (only 10 days left of 240)
    assertEquals(LIBRA, l3[5].sign)
    assertTaiwanDate(LocalDate.of(2001, 12, 4), l3[5].fromTime, 2)

    // Total 240 days, accumulated: 20+50+62.5+47.5+50 = 230, remaining 10 for Lib (out of 20)
    assertEquals(6, l3.size) // only 6 signs fit within 240 days
    // Lib is truncated: should be ~10 days instead of full 20
    val libDuration = l3[5].toTime.value - l3[5].fromTime.value
    assertEquals(10.0, libDuration, 0.01)
  }

  @Test
  fun `L4 periods within L3 Taurus under L2 Taurus under L1 Aquarius`() {
    // L3 Taurus starts at same time as L2 Taurus
    val l1AquStart = GmtJulDay(birthTime.value + 27.0 * EGYPTIAN_YEAR_DAYS)
    val l3TauStart = GmtJulDay(l1AquStart.value + (30 + 12 + 15) * EGYPTIAN_MONTH_DAYS)
    val l3TauDuration = 8.0 * levelUnitDays(3) // 8 × 2.5 = 20 days

    val l4 = generateSubPeriods(TAURUS, l3TauStart, l3TauDuration, 4)

    val l4Unit = levelUnitDays(4) // 5/24 days per year ≈ 0.2083 days

    // L4 Tau: 8 × 5/24 = 40/24 ≈ 1.667 days
    // Starts Apr 18, 2001, 00:00 TW
    assertEquals(TAURUS, l4[0].sign)
    assertEquals(4, l4[0].level)
    assertEquals(8 * l4Unit, l4[0].toTime.value - l4[0].fromTime.value, 0.001)
    assertTaiwanDateTime(LocalDateTime.of(2001, 4, 18, 0, 0), l4[0].fromTime)

    // L4 Gem: 20 × 5/24 = 100/24 ≈ 4.167 days
    // Starts Apr 19, 2001, 16:00 TW
    assertEquals(GEMINI, l4[1].sign)
    assertEquals(20 * l4Unit, l4[1].toTime.value - l4[1].fromTime.value, 0.001)
    assertTaiwanDateTime(LocalDateTime.of(2001, 4, 19, 16, 0), l4[1].fromTime)

    // L4 Can: 25 × 5/24 = 125/24 ≈ 5.208 days
    // Starts Apr 23, 2001, 20:00 TW
    assertEquals(CANCER, l4[2].sign)
    assertEquals(25 * l4Unit, l4[2].toTime.value - l4[2].fromTime.value, 0.001)
    assertTaiwanDateTime(LocalDateTime.of(2001, 4, 23, 20, 0), l4[2].fromTime)

    // L4 Leo: 19 × 5/24 = 95/24 ≈ 3.958 days
    // Starts Apr 29, 2001, 01:00 TW
    assertEquals(LEO, l4[3].sign)
    assertEquals(19 * l4Unit, l4[3].toTime.value - l4[3].fromTime.value, 0.001)
    assertTaiwanDateTime(LocalDateTime.of(2001, 4, 29, 1, 0), l4[3].fromTime)

    // L4 Vir: 20 × 5/24 = 100/24 ≈ 4.167 days
    // Starts May 3, 2001, 00:00 TW
    assertEquals(VIRGO, l4[4].sign)
    assertEquals(20 * l4Unit, l4[4].toTime.value - l4[4].fromTime.value, 0.001)
    assertTaiwanDateTime(LocalDateTime.of(2001, 5, 3, 0, 0), l4[4].fromTime, 2)

    // L4 Lib: 8 × 5/24 = 40/24 ≈ 1.667 days (but truncated)
    // Starts May 7, 2001, 04:00 TW
    assertEquals(LIBRA, l4[5].sign)
    assertTaiwanDateTime(LocalDateTime.of(2001, 5, 7, 4, 0), l4[5].fromTime, 2)

    // Accumulated: 40/24 + 100/24 + 125/24 + 95/24 + 100/24 = 460/24 ≈ 19.167 days
    // Remaining from 20 days: 0.833 days ≈ 20 hours
    // Lib is truncated to ~0.833 days (out of full 1.667 days)
    val libDuration = l4[5].toTime.value - l4[5].fromTime.value
    assertEquals(20.0 - 460.0 / 24.0, libDuration, 0.01)

    assertEquals(6, l4.size)
  }
}
