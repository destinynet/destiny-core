/**
 * Created by smallufo on 2017-09-29.
 */
package destiny.core.calendar

import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.JulianFields.JULIAN_DAY
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstantTest {

  private val logger = KotlinLogging.logger { }

  private val julDayResolver = JulDayResolver1582CutoverImpl()

  /**
   * 日本時間比台灣快一小時
   * 因此，雖然 LDT 不一樣，但是轉換成 Instant 之後，卻都是相同的
   */
  @Test
  fun testInstant() {
    val ldt1 = LocalDateTime.of(2017, 9, 28, 12, 0)
    val instant1 = ldt1.atZone(ZoneId.of("Asia/Taipei")).toInstant()
    logger.info("instant from Taiwan : {}", instant1)

    val ldt2 = LocalDateTime.of(2017, 9, 28, 13, 0)
    val instant2 = ldt2.atZone(ZoneId.of("Asia/Tokyo")).toInstant()
    logger.info("instant from Japan : {}", instant2)

    assertEquals(instant1, instant2)

    val ldt3 = LocalDateTime.of(1974, 7, 1, 12, 0)
    val instant3 = ldt3.atZone(ZoneId.of("Asia/Taipei")).toInstant()
    logger.info("台灣施行日光節約時間 : {}", instant3)

    val ldt4 = LocalDateTime.of(1974, 7, 1, 12, 0)
    val instant4 = ldt4.atZone(ZoneId.of("Asia/Tokyo")).toInstant()
    logger.info("此時就與日本時間同步 : {}", instant4)

    assertEquals(instant3, instant4)
  }

  /**
   * 測試 Instant 是否能正確跨越 1582-10-04 到 1582-10-15
   */
  @Test
  fun testInstantEpochSecond() {
    val firstDay = LocalDateTime.of(1582, 10, 15, 0, 0)
    val instant = firstDay.atZone(ZoneId.of("GMT")).toInstant()
    logger.info("1582-10-15 instant = {} , 秒數 = {}", instant, instant.epochSecond)

    // 減去一秒
    val instant2 = instant.minusSeconds(1)
    logger.info("減去一秒 , instant2 = {} ", instant2)


    logger.info(
      "Greg 前一秒 : {}",
      DateHourMinSecDecorator.getOutputString(instant2.atZone(ZoneId.of("GMT")).toLocalDateTime(), Locale.TAIWAN)
    )

    assertEquals(LocalDateTime.of(1582, 10, 14, 23, 59, 59).toEpochSecond(ZoneOffset.UTC), instant2.epochSecond)
  }

  /**
   * Instant 取得 julian day
   */
  @Test
  fun testInstantToJulianDay() {


    // 1582-10-15 開始 Gregorian Calendar , 真正 julDay = 2299160.5
    // 先取得當日零時的 julDay值 , 其值為真正需要的值加了 0.5 , 變成 2299161 . 因此最後需要減去 0.5

    val REAL_JUL_DAY_PLUS_HALF: Long = 2299161

    // 當天午夜
    var instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT)
    var halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF, halfAddedJulDay)
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5, TimeTools.getJulDay(instant).value)        // jul day + 0

    // 當天 6:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + 60 * 60 * 6)
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF, halfAddedJulDay)
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.25, TimeTools.getJulDay(instant).value)  // jul day + 0.25

    // 當天中午 12:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + 60 * 60 * 12)
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF, halfAddedJulDay)
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.5, TimeTools.getJulDay(instant).value)   // jul day + 0.5

    // 當天下午 18:00
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + 60 * 60 * 18)
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF, halfAddedJulDay)
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 0.75, TimeTools.getJulDay(instant).value)   // jul day + 0.75

    // 當天下午 23:59
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + (60 * 60 * 23).toLong() + (60 * 59).toLong() + 59)
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF, halfAddedJulDay)

    // 當天晚上 24:00 , julDay 就 +1
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT + (60 * 60 * 23).toLong() + (60 * 59).toLong() + 60)
    halfAddedJulDay = instant.atZone(ZoneId.of("GMT")).getLong(JULIAN_DAY)
    assertEquals(REAL_JUL_DAY_PLUS_HALF + 1, halfAddedJulDay)
    assertEquals(REAL_JUL_DAY_PLUS_HALF - 0.5 + 1, TimeTools.getJulDay(instant).value)   // jul day + 1
  }

  /**
   * 1582-10-15
   * Gregorian Cal 開始的 instant ，轉換到 LocalDateTime
   */
  @Test
  fun testInstantToLocalDateTime() {

    // Gregorian Cal 開始的 instant ，轉換到 LocalDateTime
    var instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT)
    var dateTime = TimeTools.getLocalDateTime(instant, julDayResolver)
    logger.info("dateTime , class = {} , value = {}", dateTime.javaClass, dateTime)
    assertTrue(dateTime is LocalDateTime)
    assertEquals(LocalDateTime.of(1582, 10, 15, 0, 0), dateTime)

    // Gregorian Cal 前一秒，變成 Julian Day 1582-10-4 23:59:59
    instant = Instant.ofEpochSecond(GREGORIAN_START_INSTANT - 1)

    dateTime = TimeTools.getLocalDateTime(instant, julDayResolver)

    logger.info("dateTime , class = {} , value = {}", dateTime.javaClass, dateTime)
    assertTrue(dateTime is JulianDateTime)
    assertEquals(JulianDateTime.of(1582, 10, 4, 23, 59, 59), dateTime)

  }

  companion object {

    /**
     * 西元 1582-10-15 0:0 的 instant 「秒數」為 -12219292800L  (from 1970-01-01 逆推)
     */
    private const val GREGORIAN_START_INSTANT = Constants.CutOver1582.FROM_UNIXEPOCH_SECONDS
  }

}
