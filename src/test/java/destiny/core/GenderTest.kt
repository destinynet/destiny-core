package destiny.core

import kotlin.test.Test
import kotlin.test.assertEquals

class GenderTest {
  @Test
  fun testToString() {
    var gender = Gender.男
    assertEquals(gender.toString(), "男")
    gender = Gender.女
    assertEquals(gender.toString(), "女")
  }

}
