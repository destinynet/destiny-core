/** 2009/10/20 下午10:53:51 by smallufo  */
package destiny.tools

import java.time.ZoneId
import java.time.zone.ZoneRulesException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class TimeZoneTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testTimeZone() {
    val tp = TimeZone.getTimeZone("Asia/Taipei")
    val sh = TimeZone.getTimeZone("Asia/Shanghai")

    assertNotEquals(tp, sh)
  }


  /**
   * zoneId count = 595
   *
   * ZoneId 可以完全轉換到 TimeZone
   */
  @Test
  fun test_ZoneId_to_TimeZone() {
    logger.info("zoneId count = {}", ZoneId.getAvailableZoneIds().size)

    for (id in ZoneId.getAvailableZoneIds()) {
      val tz = TimeZone.getTimeZone(id)
      assertNotNull(tz)
      logger.info("id = {} , \ttz.displayName = {}", id, tz.getDisplayName(Locale.TAIWAN))
    }
  }

  /**
   * TimeZone count = 623
   * 但 ZoneID 只有 595 個
   * 有許多 TimeZone 無法轉換到 ZoneID
   * 這些「都是」 3-bytes 的 TimeZoneID
   * 例如： CTT、 PST、 EST 等許多 3-bytes 的 TimeZoneID
   * 因為這些並非 unique , 見 [ZoneId.of] 說明
   *
   * 但「並非所有 3-bytes」的 id 都無法轉到 ZoneID , 許多仍可轉換，例如 GMT , UTC 仍可用
   * 參照 http://stackoverflow.com/a/41683097/298430
   */
  @Test
  fun test_TimeZone_to_ZoneId() {
    logger.info("TimeZone count = {}", TimeZone.getAvailableIDs().size)
    for (id in TimeZone.getAvailableIDs()) {
      try {
        val zoneId = ZoneId.of(id)
        logger.info("id = {} , zoneId = {}", id, zoneId)
      } catch (e: ZoneRulesException) {
        logger.warn("Cannot get ZoneId from {}", id)
        if (id.length != 3) {
          logger.error("{} 長度不為 3 !", id)
        }
      }

    }
  }

  /**
   * 因為 ZoneId.of(PST) 西岸（太平洋）時區， 無此值
   * 想得知 TimeZone(PST) 到 ZoneId 會變成 洛杉磯時區
   *
   * ZoneId.of(EST) 東岸時區， 無此值
   * TimeZone(EST) 到 ZoneId 會變成 "-5:00"
   *
   * 這些轉換，定義在 [ZoneId.SHORT_IDS] 裡面
   */
  @Test
  fun test_TimeZone_to_ZoneId_incompatibilities() {
    assertEquals("America/Los_Angeles", TimeZone.getTimeZone("PST").toZoneId().toString())

    // 本來是 "-5:00" , 換 JDK 就變成 "America/Panama"
    assertEquals("America/Panama", TimeZone.getTimeZone("EST").toZoneId().toString())
  }


  /**
   * 東八區的 tz , display name
   */
  @Test
  fun testZoneIdDisplayName() {
    for (id in TimeZone.getAvailableIDs(8 * 60 * 60 * 1000)) {
      try {
        val zoneId = ZoneId.of(id)
        logger.info("id = {} , \t\tzoneId = {}", id, zoneId)
        assertEquals(id, zoneId.toString())
      } catch (e: ZoneRulesException) {
        logger.error("無法從 {} 找到 ZoneId : {}", id, e.message)
      }

    }
  }

  @Test
  fun testSingapore() {
    val tz = TimeZone.getTimeZone("Asia/Singapore")
    logger.info("tz of singapore = {}", tz)

    var cal: GregorianCalendar

    cal = GregorianCalendar(1981, 12 - 1, 31, 23, 0)
    logger.info("(1) 1981/12/31  23:00 : DST ? {} , offset={} , date = {} , millis = {}",
      tz.inDaylightTime(cal.time), tz.getOffset(cal.timeInMillis), cal.time, cal.timeInMillis)


    cal = GregorianCalendar(1981, 12 - 1, 31, 23, 59)
    logger.info("(2) 1981/12/31  23:59 : DST ? {} , offset={} , date = {} , millis = {}",
      tz.inDaylightTime(cal.time), tz.getOffset(cal.timeInMillis), cal.time, cal.timeInMillis)


    cal = GregorianCalendar(1982, 0, 1, 0, 0)
    logger.info("(3) 1982/01/01  00:00 : DST ? {} , offset={} , date = {} , millis = {}",
      tz.inDaylightTime(cal.time), tz.getOffset(cal.timeInMillis), cal.time, cal.timeInMillis)


    cal = GregorianCalendar(1982, 0, 1, 0, 29, 59)
    logger.info("(4) 1982/01/01  00:29 : DST ? {} , offset={} , date = {} , millis = {}",
      tz.inDaylightTime(cal.time), tz.getOffset(cal.timeInMillis), cal.time, cal.timeInMillis)


    cal = GregorianCalendar(1982, 0, 1, 0, 30, 0)
    logger.info("(5) 1982/01/01  00:30 : DST ? {} , offset={} , date = {} , millis = {}",
      tz.inDaylightTime(cal.time), tz.getOffset(cal.timeInMillis), cal.time, cal.timeInMillis)


    cal = GregorianCalendar()
    cal.timeZone = TimeZone.getTimeZone("Asia/Singapore")
    cal.timeInMillis = 378664200000L
    logger.info("{}", cal.time)

    cal.timeInMillis = 378664200000L - 1000
    logger.info("{}", cal.time)


  }

  @Test
  fun testTaiwan() {
    val tp = TimeZone.getTimeZone("Asia/Taipei")
    println(tp)

    var cal: GregorianCalendar

    cal = GregorianCalendar(1945, 4 - 1, 30, 23, 59)
    println("(1) 1945/04/30  23:59 : DST ? " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + ", date = " + cal.time + " , millis = " + cal.timeInMillis)

    //根據資料：台灣於 1945/5/1 凌晨 0 時進入 DST , 撥快一小時
    cal = GregorianCalendar(1945, 5 - 1, 1, 0, 0)
    println("(2) 1945/05/01  00:00 : DST ? " + tp.inDaylightTime(cal.time) + " , " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + " , date = " + cal.time + " , millis = " + cal.timeInMillis)

    cal = GregorianCalendar(1945, 5 - 1, 1, 0, 1)
    println("(3) 1945/05/01  00:01 : DST ? " + tp.inDaylightTime(cal.time) + " , " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + " , date = " + cal.time + " , millis = " + cal.timeInMillis)

    //根據資料：台灣於 1945/10/1 凌晨 0 時離開 DST , 結束日光節約時間
    cal = GregorianCalendar(1945, 9 - 1, 30, 22, 59)
    println("(4) 1945/09/30  22:59 : DST ? " + tp.inDaylightTime(cal.time) + " , " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + " , date = " + cal.time + " , millis = " + cal.timeInMillis)

    //注意，這裡要小心！有點詭異。為什麼晚上 23 點就結束 DST ??

    cal = GregorianCalendar(1945, 9 - 1, 30, 23, 0)
    println("(5) 1945/09/30  23:00 : DST ? " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + " , date = " + cal.time + " , millis = " + cal.timeInMillis)

    cal = GregorianCalendar(1945, 10 - 1, 1, 0, 0)
    println("(6) 1945/10/01  00:00 : DST ? " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + " , date = " + cal.time + " , millis = " + cal.timeInMillis)
  }

  /**
   * 民國63年至64年（西元1974-1975年）    日光節約時間    4月1日至9月30日
   */
  @Test
  fun testTaiwan63() {
    val tp = TimeZone.getTimeZone("Asia/Taipei")
    println(tp)
    val gmt = TimeZone.getTimeZone("GMT")

    val cal: GregorianCalendar


    cal = GregorianCalendar(gmt, Locale.UK)


    println(cal)
    println(gmt.getOffset(Calendar.ZONE_OFFSET.toLong()))
    println(tp.getOffset(Calendar.ZONE_OFFSET.toLong()))

    println("日光節約時間前")
    cal.set(1974, 3 - 1, 31, 15, 59, 59)
    println(cal.time.toString() + " , " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + ", date = " + cal.time + " , millis = " + cal.timeInMillis)

    println("開始日光節約時間")
    cal.set(1974, 3 - 1, 31, 16, 0, 0)
    println("hour = " + cal.get(Calendar.HOUR_OF_DAY))
    println(cal.toString() + " , " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + ", date = " + cal.time + " , millis = " + cal.timeInMillis)

    //日光節約時間中
    cal.set(1974, 3 - 1, 31, 16, 1, 0)
    println(cal.time.toString() + " , " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + ", date = " + cal.time + " , millis = " + cal.timeInMillis)

    cal.set(1974, 3 - 1, 31, 16, 59, 0)
    println(cal.time.toString() + " , " + tp.inDaylightTime(cal.time) + ", " + tp.getOffset(
      cal.timeInMillis) + " rawOffset = " + tp.rawOffset + ", date = " + cal.time + " , millis = " + cal.timeInMillis)
  }

}

