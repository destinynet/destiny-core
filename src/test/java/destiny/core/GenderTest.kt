package destiny.core

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GenderTest {
  @Test
  fun testToString() {
    var gender = Gender.男
    assertEquals(gender.toString(Locale.TAIWAN), "男")
    gender = Gender.女
    assertEquals(gender.toString(Locale.TAIWAN), "女")
  }

}
