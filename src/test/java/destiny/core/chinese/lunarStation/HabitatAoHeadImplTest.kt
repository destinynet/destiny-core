/**
 * Created by smallufo on 2021-03-17.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import kotlin.test.Test
import kotlin.test.assertTrue

internal class HabitatAoHeadImplTest {

  val impl = HabitatAoHeadImpl()

  @Test
  fun testHabitats() {
    LunarStation.values.forEach { ls ->
      val habitats = impl.getHabitats(ls)
      assertTrue(habitats.isNotEmpty())
      println("$ls(${ls.animal}) : ${habitats.joinToString(",")}")
    }
  }
}
