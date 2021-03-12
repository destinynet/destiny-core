/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class LunarStationTest {

  @Test
  fun testLoop() {
    assertSame(LunarStation.亢, LunarStation.角.next)
    assertSame(LunarStation.軫, LunarStation.角.prev)

    assertSame(LunarStation.亢, LunarStation.角.next(1))
    assertSame(LunarStation.軫, LunarStation.角.prev(1))

    assertSame(LunarStation.角, LunarStation.軫.next)
    assertSame(LunarStation.翼, LunarStation.軫.prev)

    assertSame(LunarStation.角, LunarStation.軫.next(1))
    assertSame(LunarStation.翼, LunarStation.軫.prev(1))

    assertSame(LunarStation.角, LunarStation.角.next(28))
    assertSame(LunarStation.角, LunarStation.角.prev(28))
    assertSame(LunarStation.軫, LunarStation.軫.next(28))
    assertSame(LunarStation.軫, LunarStation.軫.prev(28))

    assertSame(LunarStation.角, LunarStation.角.next(2800))
    assertSame(LunarStation.角, LunarStation.角.prev(2800))
    assertSame(LunarStation.軫, LunarStation.軫.next(2800))
    assertSame(LunarStation.軫, LunarStation.軫.prev(2800))
  }

  @Test
  fun testName() {

    LunarStation::class.sealedSubclasses.map { k ->
      k.objectInstance as LunarStation
    }.forEach { station ->
      assertTrue(station.toString(Locale.TAIWAN).length == 1)
      assertTrue(station.toString(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.toString(Locale.ENGLISH))
    }
  }

  @Test
  fun testAnimal() {
    LunarStation::class.sealedSubclasses.map { k ->
      k.objectInstance as LunarStation
    }.forEach { station ->
      assertTrue(station.animal(Locale.TAIWAN).length == 1)
      assertTrue(station.animal(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.animal(Locale.ENGLISH))
    }
  }
}
