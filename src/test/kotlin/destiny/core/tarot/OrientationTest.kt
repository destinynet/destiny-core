/**
 * Created by smallufo on 2023-11-25.
 */
package destiny.core.tarot

import destiny.core.EnumTest
import java.util.*
import kotlin.test.Test


class OrientationTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Orientation::class, locales = listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPAN))
  }
}
