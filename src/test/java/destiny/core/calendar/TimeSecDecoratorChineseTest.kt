/**
 * @author smallufo
 * Created on 2007/3/20 at 上午 6:39:27
 */
package destiny.core.calendar

import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.threeten.extra.chrono.JulianEra
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.IsoEra
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeSecDecoratorChineseTest {

  private val logger = KotlinLogging.logger {  }


  @Test
  fun testGetOutputString() {
    val decorator = TimeSecDecoratorChinese()


    decorator.getOutputString(LocalDateTime.of(2000, 1, 1, 0, 0, 0)).also {
      assertEquals("西元　2000年01月01日　00時00分 00.00秒", it)
    }

    decorator.getOutputString(LocalDateTime.of(LocalDate.of(2000, 12, 31).with(IsoEra.BCE), LocalTime.of(23, 59, 59, 999000000))).also {
      assertEquals("西元前2000年12月31日　23時59分 59.99秒", it)
    }

    decorator.getOutputString(JulianDateTime.of(2000, 1, 1, 0, 0, 0)).also {
      assertEquals("西元　2000年01月01日　00時00分 00.00秒" , it)
    }

    decorator.getOutputString(JulianDateTime.of(-2000, 1, 1, 0, 0, 0)).also {
      assertEquals("西元前2001年01月01日　00時00分 00.00秒" , it)
    }

    JulianDateTime.of(2000, 1, 1, 0, 0, 0).toLocalDate().era.also {
      assertEquals(JulianEra.AD , it)
    }

    JulianDateTime.of(-2000, 1, 1, 0, 0, 0).toLocalDate().era.also {
      assertEquals(JulianEra.BC , it)
    }
  }

}
