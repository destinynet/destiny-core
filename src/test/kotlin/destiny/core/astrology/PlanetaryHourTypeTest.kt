/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.EnumTest
import kotlin.test.Test

internal class PlanetaryHourTypeTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(PlanetaryHourType::class)
  }
}
