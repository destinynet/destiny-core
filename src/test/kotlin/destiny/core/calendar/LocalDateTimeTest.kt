/**
 * Created by smallufo on 2015-05-14.
 */
package destiny.core.calendar

import destiny.tools.KotlinLogging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.IsoEra
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField.YEAR_OF_ERA
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class LocalDateTimeTest {

  private val logger = KotlinLogging.logger { }

  /** 秒以下到微秒（6 位）都能解析 */
  @Test
  fun testParse2() {
    assertEquals(
      LocalDateTime.of(2019, 4, 2, 18, 4, 37, 628_251_000),
      LocalDateTime.parse("2019-04-02T18:04:37.628251")
    )
  }

  /** 無秒數的 ISO 字串：預設 parser 與 [DateTimeFormatter.ISO_DATE_TIME] 結果相同 */
  @Test
  fun testParse() {
    // trump : 1946-06-14T12:30
    val trump = "1946-06-14T12:30"
    val expected = LocalDateTime.of(1946, 6, 14, 12, 30)

    assertEquals(expected, LocalDateTime.parse(trump))
    assertEquals(expected, LocalDateTime.parse(trump, DateTimeFormatter.ISO_DATE_TIME))
  }

  /** `withXxx` 回傳新物件，原物件不變（immutable）。原本用 now() 且只有 log，不可重現也驗不到東西 */
  @Test
  fun testWithValue() {
    val ldt = LocalDateTime.of(2019, 4, 2, 18, 4, 37)
    val ldt2 = ldt.withYear(2000)

    assertEquals(2019, ldt.year)
    assertEquals(LocalDateTime.of(2000, 4, 2, 18, 4, 37), ldt2)
    assertNotSame(ldt, ldt2)
  }


  @Test
  fun testOutput() {
    assertEquals("2012-06-21", LocalDate.of(2012, 6, 21).format(DateTimeFormatter.ofPattern("uuuu-MM-dd")))
    assertEquals("2012-06", LocalDate.of(2012, 6, 21).format(DateTimeFormatter.ofPattern("uuuu-MM")))
  }

  /**
   * [LocalDateTime] 採 ISO proleptic 曆法，**不理會 1582 的 Julian→Gregorian cutover**：
   * 從 1582-10-16 往前退 10 天，會平順地走到 1582-10-06，
   * 而不是像 [java.util.GregorianCalendar] 那樣跳過 10/05～10/14。
   *
   * 需要 cutover 語意時得改用 `JulDayResolver1582CutoverImpl`（見 [JulDayResolver1582ImplTest]）。
   * 原本這裡只是把 10 天印出來，這個結論並沒有被驗證。
   */
  @Test
  fun testLocalDateTime1582() {
    val tz = ZoneId.of("Asia/Taipei")
    var zdt = LocalDateTime.of(1582, 10, 16, 0, 0).atZone(tz)

    val dates = (0..9).map {
      zdt = ZonedDateTime.from(zdt).minusDays(1)
      zdt.toLocalDate()
    }

    assertEquals((15 downTo 6).map { LocalDate.of(1582, 10, it) }, dates)
  }

  /**
   * 西元元年之前
   *
   *
   * 0001-01-02 : era = CE , year = 1 , year_of_era = 1 , toEpochDay = -719161
   * 0001-01-01 : era = CE , year = 1 , year_of_era = 1 , toEpochDay = -719162
   * 0000-12-31 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719163
   * 0000-12-30 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719164
   * 0000-12-29 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719165
   * 0000-12-28 : era = BCE , year = 0 , year_of_era = 1 , toEpochDay = -719166
   *
   */
  @Test
  fun test_LocalDate_BC() {
    var ld = LocalDate.of(1, 1, 3)
    // 西元 1年 1月 3日

    ld = ld.minusDays(1)
    // 西元 1年 1月 2日
    assertSame(IsoEra.CE, ld.era)
    assertEquals(1, ld.year.toLong())
    assertEquals(1, ld.get(YEAR_OF_ERA).toLong())
    assertEquals(1, ld.monthValue.toLong())
    assertEquals(-719161, ld.toEpochDay())

    ld = ld.minusDays(1)
    // 西元 1年 1月 1日
    assertSame(IsoEra.CE, ld.era)
    assertEquals(1, ld.year.toLong())
    assertEquals(1, ld.get(YEAR_OF_ERA).toLong())
    assertEquals(1, ld.monthValue.toLong())
    assertEquals(-719162, ld.toEpochDay())

    ld = ld.minusDays(1)
    // 西元前 1年 12月 31日
    assertSame(IsoEra.BCE, ld.era)
    assertEquals(0, ld.year.toLong())         // year 是連續的，因此「西元前一年」， year = 0
    assertEquals(1, ld.get(YEAR_OF_ERA).toLong())  // year_of_era 一定大於0
    assertEquals(12, ld.monthValue.toLong())
    assertEquals(-719163, ld.toEpochDay())

    ld = ld.minusDays(1)
    // 西元前 1年 12月 30日
    assertSame(IsoEra.BCE, ld.era)
    assertEquals(0, ld.year.toLong())         // year 是連續的，因此「西元前一年」， year = 0
    assertEquals(1, ld.get(YEAR_OF_ERA).toLong())  // year_of_era 一定大於0
    assertEquals(12, ld.monthValue.toLong())
    assertEquals(-719164, ld.toEpochDay())


  }

  /**
   * 比對 year , 以及 year_of_era 的差異
   * 0001-01-02T00:00 : era = CE , year = 1 , year_of_era = 1
   * 0001-01-01T00:00 : era = CE , year = 1 , year_of_era = 1
   * 0000-12-31T00:00 : era = BCE , year = 0 , year_of_era = 1
   * 0000-12-30T00:00 : era = BCE , year = 0 , year_of_era = 1
   * 0000-12-29T00:00 : era = BCE , year = 0 , year_of_era = 1
   * 0000-12-28T00:00 : era = BCE , year = 0 , year_of_era = 1
   */
  @Test
  fun test_LocalDateTime_BC() {
    // (LocalDateTime , era , year , year_of_era) —— 即 KDoc 那張表
    val expected = listOf(
      Triple(LocalDateTime.of(1, 1, 2, 0, 0), IsoEra.CE, 1),
      Triple(LocalDateTime.of(1, 1, 1, 0, 0), IsoEra.CE, 1),
      Triple(LocalDateTime.of(0, 12, 31, 0, 0), IsoEra.BCE, 0),
      Triple(LocalDateTime.of(0, 12, 30, 0, 0), IsoEra.BCE, 0),
      Triple(LocalDateTime.of(0, 12, 29, 0, 0), IsoEra.BCE, 0),
      Triple(LocalDateTime.of(0, 12, 28, 0, 0), IsoEra.BCE, 0),
    )

    var ldt = LocalDateTime.of(1, 1, 3, 0, 0)
    expected.forEach { (expectedLdt, expectedEra, expectedYear) ->
      ldt = ldt.minusDays(1)
      assertEquals(expectedLdt, ldt)
      assertSame(expectedEra, ldt.toLocalDate().era)
      assertEquals(expectedYear, ldt.year)      // year 連續，故「西元前一年」的 year = 0
      assertEquals(1, ldt.get(YEAR_OF_ERA))     // year_of_era 恆大於 0
    }
  }

  @Test
  fun testEra_Compare() {
    val now = LocalDateTime.now()
    assertSame(IsoEra.CE, now.toLocalDate().era) // 現在應該是西元後

    val ce = LocalDateTime.of(1, 1, 1, 0, 0, 0) // 西元第一秒
    assertSame(IsoEra.CE, ce.toLocalDate().era)

    val bce = LocalDateTime.from(ce).minusSeconds(1) // 西元前最後一秒
    assertSame(IsoEra.BCE, bce.toLocalDate().era)
  }


  @Test
  fun testEpochSecond() {
    val ldt = LocalDateTime.of(1970, 1, 1, 0, 0)
    logger.info("ldt = {}", ldt)
    assertEquals(0, ldt.atZone(GMT).toEpochSecond())
    assertEquals((-60 * 60 * 8).toLong(), ldt.atZone(ZoneId.of("Asia/Taipei")).toEpochSecond())

  }


  @Test
  fun testEra() {

    var localDate: LocalDate = LocalDate.of(1, 1, 1)
    assertSame(IsoEra.CE, localDate.era)

    localDate = localDate.minus(1, ChronoUnit.DAYS)
    assertSame(IsoEra.BCE, localDate.era)
  }

}
