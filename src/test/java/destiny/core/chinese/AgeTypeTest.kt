/**
 * Created by smallufo on 2022-06-04.
 */
package destiny.core.chinese

import destiny.core.EnumTest
import kotlin.test.Test

internal class AgeTypeTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(AgeType::class)
  }
}
