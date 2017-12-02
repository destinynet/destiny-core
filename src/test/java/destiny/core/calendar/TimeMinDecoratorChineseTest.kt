/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.IsoEra
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeMinDecoratorChineseTest {

  @Test
  fun getOutputString() {
    val decorator = TimeMinDecoratorChinese()

    var time: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
    assertEquals("西元　2000年01月01日　00時00分" , decorator.getOutputString(time))

    time = LocalDateTime.of(LocalDate.of(2000, 12, 31).with(IsoEra.BCE), LocalTime.of(23, 59, 59, 999000000))
    assertEquals("西元前2000年12月31日　23時59分", decorator.getOutputString(time))
  }

}