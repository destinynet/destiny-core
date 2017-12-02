/**
 * @author smallufo
 * Created on 2007/3/20 at 上午 6:39:27
 */
package destiny.core.calendar

import org.junit.Assert.assertEquals
import org.junit.Test
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.IsoEra

class TimeSecDecoratorChineseTest {

  private val logger = LoggerFactory.getLogger(javaClass)


  @Test
  fun testGetOutputString() {
    val decorator = TimeSecDecoratorChinese()

    var gTime: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
    logger.info("{} : {}", gTime, decorator.getOutputString(gTime))
    assertEquals("西元　2000年01月01日　00時00分 00.00秒", decorator.getOutputString(gTime))

    gTime = LocalDateTime.of(LocalDate.of(2000, 12, 31).with(IsoEra.BCE), LocalTime.of(23, 59, 59, 999000000))
    logger.info("{} : {}", gTime, decorator.getOutputString(gTime))
    assertEquals("西元前2000年12月31日　23時59分 59.99秒", decorator.getOutputString(gTime))

    var jTime: JulianDateTime = JulianDateTime.of(2000, 1, 1, 0, 0, 0.0)
    logger.info("jTime.toLocalDate.era = {}", jTime.toLocalDate().era)
    logger.info("{} : {}", jTime.javaClass.simpleName, decorator.getOutputString(jTime))

    jTime = JulianDateTime.of(-2000, 1, 1, 0, 0, 0.0)
    logger.info("jTime.toLocalDate.era = {}", jTime.toLocalDate().era)
    logger.info("{} : {}", jTime.javaClass.simpleName, decorator.getOutputString(jTime))
  }

}
