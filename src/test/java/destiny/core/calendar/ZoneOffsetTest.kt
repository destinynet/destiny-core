/**
 * Created by smallufo on 2017-09-29.
 */
package destiny.core.calendar

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Timestamp
import java.time.*
import java.time.chrono.ChronoLocalDateTime

import org.junit.Assert.assertEquals

class ZoneOffsetTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testZone() {
    val offset = ZoneOffset.of("+8")
    logger.info("offset = {}", offset)
  }


  @Test
  fun sqlTimestamp() {
    val epochLMT = LocalDateTime.of(1970, 1, 1, 0, 0)
    val epochZonedGMT = ZonedDateTime.of(epochLMT, ZoneId.of("GMT"))
    val cldt = LocalDateTime.of(1970, 1, 1, 0, 0)
    val sysDefaultZoneId = ZoneId.systemDefault()
    val GMT = ZoneId.of("GMT")


    logger.info("sysDefaultZoneId = {}", sysDefaultZoneId)
    logger.info("cldt = {}", cldt)
    logger.info("epochLMT = {}", epochLMT)
    logger.info("epochLMT.toInstant(UTC) = {}", epochLMT.toInstant(ZoneOffset.UTC))
    logger.info("epochLMT.toInstant(ZoneOffset(8HR)) = {}", epochLMT.toInstant(ZoneOffset.ofHours(8)))

    val offset = sysDefaultZoneId.rules.getOffset(epochLMT)
    logger.info("offset = {}", offset)


    val epochLmtTS = Timestamp.valueOf(epochLMT)
    logger.info("舊有演算法的 epochLmtTS = {} , getTime = {}", epochLmtTS, epochLmtTS.time)
    // 這裡會抓系統的 ZonedOffset (GMT+8) , 所以，當台灣已經到 1970-01-01 00:00 時， GMT 還有八小時，所以會傳回 -8小時的 millis 之值
    assertEquals((-8 * 60 * 60 * 1000).toLong(), epochLmtTS.time)

    val epochGmtTS = Timestamp.from(epochZonedGMT.toInstant())
    logger.info("epochGmtTS = {} , getTime = {}", epochGmtTS, epochGmtTS.time)
    assertEquals(0, epochGmtTS.time) // GMT 定義，就是傳回 0

    val newTS = Timestamp.from(cldt.atZone(sysDefaultZoneId).toInstant())
    logger.info("新演算法 : {} , getTime = {}", newTS, newTS.time)

  }
}
