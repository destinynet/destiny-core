/**
 * Created by smallufo on 2021-10-23.
 */
package destiny.core.astrology

import destiny.core.EnumTest
import kotlin.test.Test

internal class DayNightImplTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(DayNightImpl::class)
  }
}
