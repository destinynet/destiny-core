/**
 * Created by smallufo on 2021-09-13.
 */
package destiny.core

import destiny.core.IntAgeNoteImpl.Minguo
import destiny.core.IntAgeNoteImpl.WestYear
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class IntAgeNoteImplTest {

  @Test
  fun getTitle() {
    assertEquals("民國", Minguo.toString(Locale.TAIWAN))
    assertEquals("民国", Minguo.toString(Locale.CHINA))
    assertEquals("Minguo", Minguo.toString(Locale.ENGLISH))

    assertEquals("西元", WestYear.toString(Locale.TAIWAN))
    assertEquals("西元", WestYear.toString(Locale.CHINA))
    assertEquals("Year", WestYear.toString(Locale.ENGLISH))
  }

}
