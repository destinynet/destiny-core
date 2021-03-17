/**
 * Created by smallufo on 2021-03-18.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class AnimalDayNightAnimalStarImplTest {

  val impl = AnimalDayNightAnimalStarImpl()

  @Test
  fun testDayNight() {
    LunarStation.values.forEach { ls ->
      assertNotNull(impl.getDayNight(ls))
    }
  }
}
