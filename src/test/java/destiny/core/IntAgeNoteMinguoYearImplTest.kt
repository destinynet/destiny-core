/**
 * Created by smallufo on 2017-10-24.
 */
package destiny.core

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class IntAgeNoteMinguoYearImplTest {


  @Test
  fun getTitle() {
    val impl = IntAgeNoteMinguoYearImpl()
    assertEquals("民國", impl.toString(Locale.TAIWAN))
    assertEquals("民国", impl.toString(Locale.CHINA))
    assertEquals("Minguo", impl.toString(Locale.ENGLISH))
  }

}
