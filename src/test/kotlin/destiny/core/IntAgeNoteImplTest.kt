/**
 * Created by smallufo on 2021-09-13.
 */
package destiny.core

import destiny.core.IntAgeNote.Minguo
import destiny.core.IntAgeNote.WestYear
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class IntAgeNoteImplTest : EnumTest() {

  @Test
  fun getTitle() {
    assertEquals("民國", Minguo.getTitle(Locale.TAIWAN))
    assertEquals("民国", Minguo.getTitle(Locale.CHINA))
    assertEquals("Minguo", Minguo.getTitle(Locale.ENGLISH))

    assertEquals("西元", WestYear.getTitle(Locale.TAIWAN))
    assertEquals("西元", WestYear.getTitle(Locale.CHINA))
    assertEquals("Year", WestYear.getTitle(Locale.ENGLISH))
  }

  @Test
  fun testString() {
    testEnums(IntAgeNote::class)
  }
}
