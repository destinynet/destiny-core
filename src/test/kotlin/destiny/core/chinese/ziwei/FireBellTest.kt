/**
 * Created by smallufo on 2019-12-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.EnumTest
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class FireBellTest : EnumTest() {

  @Test
  fun testToString() {
    assertEquals("全集" , FireBell.FIREBELL_COLLECT.getTitle(Locale.TAIWAN))
    assertEquals("全書" , FireBell.FIREBELL_BOOK.getTitle(Locale.TAIWAN))

    assertEquals("全集" , FireBell.FIREBELL_COLLECT.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("全书" , FireBell.FIREBELL_BOOK.getTitle(Locale.SIMPLIFIED_CHINESE))
  }

  @Test
  fun testString() {
    testEnums(FireBell::class)
  }
}
