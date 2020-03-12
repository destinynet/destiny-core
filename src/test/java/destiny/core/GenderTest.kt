package destiny.core

import destiny.core.Gender.女
import destiny.core.Gender.男
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GenderTest {

  @Test
  fun testToString() {
    assertNotNull(男.toString())
    assertNotNull(女.toString())

    assertEquals("男", 男.toString(Locale.TAIWAN))
    assertEquals("女", 女.toString(Locale.TAIWAN))
  }

}
