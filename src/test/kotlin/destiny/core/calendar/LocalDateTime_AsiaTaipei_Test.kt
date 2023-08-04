/**
 * Created by smallufo on 2015-05-13.
 */
package destiny.core.calendar

import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * java 8 LocalDateTime 的測試
 */
class LocalDateTime_AsiaTaipei_Test {

  private val logger = KotlinLogging.logger {  }


  /**
   * 已知：
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  fun testGetGMT_from_LMT() {
    val lmt: LocalDateTime = LocalDateTime.of(1974, 3, 31, 23, 59, 59)

    logger.info("lmt = {}" , lmt)

    val tz = TimeZone.getTimeZone("Asia/Taipei")

    //日光節約時間前一秒
    lmt.atZone(tz.toZoneId()).also { zdt ->
      logger.info("zdt = {}" , zdt)

      assertEquals(ZoneOffset.ofHours(8) , zdt.offset)
      println("zoned gmt = " + zdt.withZoneSameInstant(GMT))

      zdt.withZoneSameInstant(GMT).also { gmtZdt: ZonedDateTime ->
        logger.info("zoned gmt = {}" , gmtZdt)
        assertEquals(ZonedDateTime.of(1974 , 3, 31 , 15 , 59 , 59 , 0 , GMT) , gmtZdt)
      }
    }

  }


  /**
   * 測試 Asia/Taipei 的日光節約時間切換
   */
  @Test
  fun testAsiaTaipeiDST() {
    var lmt: LocalDateTime
    var zdt: ZonedDateTime

    val asiaTaipei = ZoneId.of("Asia/Taipei")

    // ========== 民國 34 年（西元 1945 ）9月21日之前 GMT+9 , 之後 GMT+8 ==========
    // 更換時區前一小時+一秒
    lmt = LocalDateTime.of(1945, 9, 20, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1945-09-20T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，仍是 GMT+9
    zdt = zdt.plusSeconds(1)
    assertEquals("1945-09-21T00:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了59分59秒 , 還在 GMT+9
    zdt = zdt.plusMinutes(59).plusSeconds(59)
    assertEquals("1945-09-21T00:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 再加一秒，變成 GMT+8 , 同時時間倒退一小時
    zdt = zdt.plusSeconds(1)
    assertEquals("1945-09-21T00:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())


    // ========== 民國 35 年（西元 1946 ）日光節約時間 5月15日 至 9月30日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1946, 5, 14, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1946-05-14T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1946-05-15T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1946, 9, 30, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1946-09-30T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1946-09-30T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())


    // ========== 民國 36 年（西元 1947 ）日光節約時間 4月15日 至 10月31日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1947, 4, 14, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1947-04-14T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1947-04-15T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1947, 10, 31, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1947-10-31T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1947-10-31T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())


    // ========== 民國 37~40 年（西元 1948 ~ 1951 ）日光節約時間 5月1日 至 9月30日 ==========
    for (year in 1948..1951) {
      // 日光節約時間前一秒
      lmt = LocalDateTime.of(year, 4, 30, 23, 59, 59)
      zdt = lmt.atZone(asiaTaipei)
      assertEquals("$year-04-30T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
      // 過了一秒，變成日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-05-01T01:00", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())

      // 日光節約時間結束前一秒
      zdt = LocalDateTime.of(year, 9, 30, 23, 59, 59).atZone(asiaTaipei)
      assertEquals("$year-09-30T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())
      // 過了一秒，結束日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-09-30T23:00", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
    }

    // ========== 民國 41 年（西元 1952 ）日光節約時間 3月1日 至 10月31日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1952, 2, 29, 23, 59, 59) // 西元閏年
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1952-02-29T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1952-03-01T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1952, 10, 31, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1952-10-31T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plusSeconds(1)
    assertEquals("1952-10-31T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())

    // ========== 民國 42~43 年（西元 1953 ~ 1954 ）日光節約時間 4月1日 至 10月31日 ==========
    for (year in 1953..1954) {
      // 日光節約時間前一秒
      lmt = LocalDateTime.of(year, 3, 31, 23, 59, 59)
      zdt = lmt.atZone(asiaTaipei)
      assertEquals("$year-03-31T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
      // 過了一秒，變成日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-04-01T01:00", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())

      // 日光節約時間結束前一秒
      zdt = LocalDateTime.of(year, 10, 31, 23, 59, 59).atZone(asiaTaipei)
      assertEquals("$year-10-31T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())
      // 過了一秒，結束日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-10-31T23:00", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
    }


    // ========== 民國 44~48 年（西元 1955 ~ 1959 ）日光節約時間 4月1日 至 9月30日 ==========
    for (year in 1955..1959) {
      // 日光節約時間前一秒
      lmt = LocalDateTime.of(year, 3, 31, 23, 59, 59)
      zdt = lmt.atZone(asiaTaipei)
      assertEquals("$year-03-31T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
      // 過了一秒，變成日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-04-01T01:00", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())

      // 日光節約時間結束前一秒
      zdt = LocalDateTime.of(year, 9, 30, 23, 59, 59).atZone(asiaTaipei)
      assertEquals("$year-09-30T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())
      // 過了一秒，結束日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-09-30T23:00", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
    }


    // ========== 民國49年（西元1960）日光節約時間 6月1日 至 9月30日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1960, 5, 31, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1960-05-31T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1960-06-01T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1960, 9, 30, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1960-09-30T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1960-09-30T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())


    // ========== 民國50年（西元1961）日光節約時間 6月1日 至 9月30日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1961, 5, 31, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1961-05-31T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1961-06-01T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1961, 9, 30, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1961-09-30T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1961-09-30T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())


    // ========== 民國 63~64 年（西元 1974 ~ 1975 ）日光節約時間 4月1日 至 9月30日 ==========
    for (year in 1974..1975) {
      // 日光節約時間前一秒
      lmt = LocalDateTime.of(year, 3, 31, 23, 59, 59)
      zdt = lmt.atZone(asiaTaipei)
      assertEquals("$year-03-31T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
      // 過了一秒，變成日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-04-01T01:00", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())

      // 日光節約時間結束前一秒
      zdt = LocalDateTime.of(year, 9, 30, 23, 59, 59).atZone(asiaTaipei)
      assertEquals("$year-09-30T23:59:59", zdt.toLocalDateTime().toString())
      assertEquals("+09:00", zdt.offset.toString())
      // 過了一秒，結束日光節約時間
      zdt = zdt.plus(1, ChronoUnit.SECONDS)
      assertEquals("$year-09-30T23:00", zdt.toLocalDateTime().toString())
      assertEquals("+08:00", zdt.offset.toString())
    }


    // ========== 民國68年（西元1979）日光節約時間 7月1日 至 9月30日 ==========
    // 日光節約時間前一秒
    lmt = LocalDateTime.of(1979, 6, 30, 23, 59, 59)
    zdt = lmt.atZone(asiaTaipei)
    assertEquals("1979-06-30T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
    // 過了一秒，變成日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1979-07-01T01:00", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())

    // 日光節約時間結束前一秒
    zdt = LocalDateTime.of(1979, 9, 30, 23, 59, 59).atZone(asiaTaipei)
    assertEquals("1979-09-30T23:59:59", zdt.toLocalDateTime().toString())
    assertEquals("+09:00", zdt.offset.toString())
    // 過了一秒，結束日光節約時間
    zdt = zdt.plus(1, ChronoUnit.SECONDS)
    assertEquals("1979-09-30T23:00", zdt.toLocalDateTime().toString())
    assertEquals("+08:00", zdt.offset.toString())
  }

  /**
   * 1945-09-21 01:00 之前， GMT+9
   * 之後 , GMT+8
   * 測試印出每秒的 offset
   */
  @Test
  fun testTaiwan1945() {
    val lmt: LocalDateTime = LocalDateTime.of(1945, 9, 20, 23, 59, 0)
    var zdt: ZonedDateTime

    val tz = TimeZone.getTimeZone("Asia/Taipei")
    zdt = lmt.atZone(tz.toZoneId())

    do {
      zdt = zdt.plusSeconds(1)
      println("localDateTime = " + zdt.toLocalDateTime() + " , offset = " + zdt.offset)
    } while (zdt.offset.toString().equals("+09:00", ignoreCase = true))

  }
}
