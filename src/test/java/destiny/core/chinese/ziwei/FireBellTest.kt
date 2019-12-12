/**
 * Created by smallufo on 2019-12-13.
 */
package destiny.core.chinese.ziwei

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class FireBellTest {

  @Test
  fun testToString() {
    assertEquals("全集" , FireBell.FIREBELL_COLLECT.toString(Locale.TAIWAN))
    assertEquals("全書" , FireBell.FIREBELL_BOOK.toString(Locale.TAIWAN))

    assertEquals("全集" , FireBell.FIREBELL_COLLECT.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("全书" , FireBell.FIREBELL_BOOK.toString(Locale.SIMPLIFIED_CHINESE))
  }
}
