package destiny.core

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GenderTest {

  @Test
  fun testToString() {
    var gender = Gender.男
    assertEquals("男", gender.toString(Locale.TAIWAN))
    gender = Gender.女
    assertEquals("女", gender.toString(Locale.TAIWAN))
  }

}
