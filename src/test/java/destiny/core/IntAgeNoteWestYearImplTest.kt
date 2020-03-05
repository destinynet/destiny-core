/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class IntAgeNoteWestYearImplTest {

  val impl = IntAgeNoteWestYearImpl()

  @Test
  fun testAgeNote() {
    // 2019-12-31 12:00
    val dec31Noon2019 = 2458849.0
    impl.getAgeNote(dec31Noon2019).also { assertEquals("2019" , it) }
    impl.getAgeNote(dec31Noon2019+0.499).also { assertEquals("2019" , it) }
    // 2020-01-01 0:00
    impl.getAgeNote(dec31Noon2019+0.5).also { assertEquals("2020" , it) }
  }

  @Test
  fun getTitle() {
    assertEquals("西元", impl.toString(Locale.TAIWAN))
    assertEquals("西元", impl.toString(Locale.CHINA))
    assertEquals("Year", impl.toString(Locale.ENGLISH))
  }

  @Test
  fun getDesc() {
    assertEquals("西元", impl.getDescription(Locale.TAIWAN))
    assertEquals("西元", impl.getDescription(Locale.CHINA))
    assertEquals("Year", impl.getDescription(Locale.ENGLISH))
  }

}
