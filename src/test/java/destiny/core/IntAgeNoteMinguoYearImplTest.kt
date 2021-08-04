/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core

import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IntAgeNoteMinguoYearImplTest {

  val impl = IntAgeNoteMinguoYearImpl(JulDayResolver1582CutoverImpl())

  @Test
  fun testAgeNote() {
    // 2019-12-31 12:00
    val dec31Noon2019 = 2458849.0.toGmtJulDay()
    impl.getAgeNote(dec31Noon2019).also { assertEquals("民108" , it) }
    impl.getAgeNote(dec31Noon2019+0.499).also { assertEquals("民108" , it) }
    // 2020-01-01 0:00
    impl.getAgeNote(dec31Noon2019+0.5).also { assertEquals("民109" , it) }

    // 1911-12-31 12:00 民國元年前，不輸出
    val dec31Noon1911 = 2419402.0.toGmtJulDay()
    impl.getAgeNote(dec31Noon1911).also { assertNull(it) }
    impl.getAgeNote(dec31Noon1911+0.499).also { assertNull(it) }
    // 2020-01-01 0:00 , 民國元年後，輸出 民1
    impl.getAgeNote(dec31Noon1911+0.5).also { assertEquals("民1" , it) }

  }

  @Test
  fun getTitle() {
    assertEquals("民國", impl.toString(Locale.TAIWAN))
    assertEquals("民国", impl.toString(Locale.CHINA))
    assertEquals("Minguo", impl.toString(Locale.ENGLISH))
  }

  @Test
  fun getDesc() {
    assertEquals("民國", impl.getDescription(Locale.TAIWAN))
    assertEquals("民国", impl.getDescription(Locale.CHINA))
    assertEquals("Minguo", impl.getDescription(Locale.ENGLISH))
  }

}
