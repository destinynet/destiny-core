package destiny.core

import kotlin.test.Test

class GenderTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Gender::class, false)
  }


}
