/**
 * Created by smallufo on 2018-03-25.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import kotlin.test.Test
import kotlin.test.assertEquals


class DateDecoratorChineseTest {

  @Test
  fun getOutputString() {
    val decorator : Decorator<ChronoLocalDate> = DateDecoratorChinese()
    assertEquals("西元　2000年01月01日" ,   decorator.getOutputString(LocalDate.of(2000 , 1 , 1)))
    assertEquals("西元　 999年01月01日" ,   decorator.getOutputString(LocalDate.of(999 , 1 , 1)))
    assertEquals("西元　  10年01月01日" ,   decorator.getOutputString(LocalDate.of(10 , 1 , 1)))
    assertEquals("西元　   1年01月01日" ,   decorator.getOutputString(LocalDate.of(1 , 1 , 1)))
    assertEquals("西元前   1年01月01日" ,   decorator.getOutputString(LocalDate.of(0 , 1 , 1)))
    assertEquals("西元前 100年01月01日" ,   decorator.getOutputString(LocalDate.of(-99 , 1 , 1)))
    assertEquals("西元前1000年01月01日" ,   decorator.getOutputString(LocalDate.of(-999 , 1 , 1)))
  }
}