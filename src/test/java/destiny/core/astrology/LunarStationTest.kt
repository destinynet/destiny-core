/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class LunarStationTest {

  @Test
  fun testName() {
    LunarStation::class.nestedClasses.map { k ->
      k.objectInstance as LunarStation
    }.forEach { station ->
      assertTrue(station.toString(Locale.TAIWAN).length == 1)
      assertTrue(station.toString(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.toString(Locale.ENGLISH))
    }
  }

  @Test
  fun testAnimal() {
    LunarStation::class.nestedClasses.map { k ->
      k.objectInstance as LunarStation
    }.forEach { station ->
      assertTrue(station.animal(Locale.TAIWAN).length == 1)
      assertTrue(station.animal(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.animal(Locale.ENGLISH))
    }
  }
}
