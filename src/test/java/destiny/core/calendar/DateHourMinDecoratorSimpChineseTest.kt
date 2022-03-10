/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.chrono.IsoEra
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DateHourMinDecoratorSimpChineseTest {
  @Test
  fun getOutputString() {
    val decorator = DateHourMinDecoratorSimpChinese

    decorator.getOutputString(LocalDateTime.of(2000, 1, 1, 0, 0, 0)).also {
      assertEquals("西元　2000年01月01日　00时00分", it)
    }

    decorator.getOutputString(LocalDateTime.of(LocalDate.of(2000, 12, 31).with(IsoEra.BCE), LocalTime.of(23, 59, 59, 999000000))).also {
      assertEquals("西元前2000年12月31日　23时59分", it)
    }
  }
}
