/**
 * Created by smallufo on 2017-03-04.
 */
package destiny.tools

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class ZoneOffsetTest {

  private val logger = KotlinLogging.logger {  }

  @Test
  fun testOf_8H() {
    val zf = ZoneOffset.of("+8")
    logger.info("zf = {} , id = {}", zf, zf.id)
    assertEquals("+08:00", zf.id)
    assertEquals((8 * 60 * 60).toLong(), zf.totalSeconds.toLong())

    val zid = ZoneId.of("Asia/Taipei")
    zid.normalized()
  }

  /**
   * 8小時零1分
   */
  @Test
  fun testOf_8H1m() {
    val zf = ZoneOffset.of("+08:01")
    logger.info("zf = {} , id = {}", zf, zf.id)
    assertEquals("+08:01", zf.id)
    assertEquals((8 * 60 * 60 + 60).toLong(), zf.totalSeconds.toLong())
  }

  /**
   * 8小時零1分2秒
   */
  @Test
  fun testOf_8H1m2s() {
    val zf = ZoneOffset.ofHoursMinutesSeconds(8, 1, 2)
    logger.info("zf = {} , id = {}", zf, zf.id)
    assertEquals("+08:01:02", zf.id)
    assertEquals((8 * 60 * 60 + 60 + 2).toLong(), zf.totalSeconds.toLong())
  }

  /**
   * Asia/Taipei 於 1975 夏天，有實施 日光節約時間 , zf 應該為 "+09:00"
   */
  @Test
  fun testAsiaTaipei() {
    var lmt = LocalDateTime.of(1976, 6, 1, 0, 0)
    var zf = lmt.atZone(ZoneId.of("Asia/Taipei")).offset
    logger.debug("zf = {} , id = {} , 秒數 = {}", zf, zf.id, zf.totalSeconds)
    assertEquals("+08:00", zf.id)
    assertEquals((8 * 60 * 60).toLong(), zf.totalSeconds.toLong())

    lmt = LocalDateTime.of(1975, 6, 1, 0, 0)
    zf = lmt.atZone(ZoneId.of("Asia/Taipei")).offset
    logger.debug("zf = {} , id = {} , 秒數 = {}", zf, zf.id, zf.totalSeconds)
    assertEquals("+09:00", zf.id)
    assertEquals((9 * 60 * 60).toLong(), zf.totalSeconds.toLong())
  }
}
