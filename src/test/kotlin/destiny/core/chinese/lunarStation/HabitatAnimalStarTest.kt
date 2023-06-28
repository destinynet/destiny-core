/**
 * Created by smallufo on 2021-03-18.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import kotlin.test.Test
import kotlin.test.assertTrue

internal class HabitatAnimalStarTest {
  val impl = HabitatAnimalStar()

  @Test
  fun testHabitats() {
    LunarStation.list.forEach { ls ->
      val habitats = impl.getHabitats(ls)
      assertTrue(habitats.isNotEmpty())
      println("$ls(${ls.animal}) : ${habitats.joinToString(",")}")
    }
  }
}
