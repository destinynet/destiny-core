/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core.astrology

import destiny.core.astrology.LunarStation.*
import destiny.core.chinese.toString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class LunarStationTest {

  @Test
  fun testLoop() {
    assertSame(亢, 角.next)
    assertSame(軫, 角.prev)

    assertSame(亢, 角.next(1))
    assertSame(軫, 角.prev(1))

    assertSame(角, 軫.next)
    assertSame(翼, 軫.prev)

    assertSame(角, 軫.next(1))
    assertSame(翼, 軫.prev(1))

    assertSame(角, 角.next(28))
    assertSame(角, 角.prev(28))
    assertSame(軫, 軫.next(28))
    assertSame(軫, 軫.prev(28))

    assertSame(角, 角.next(2800))
    assertSame(角, 角.prev(2800))
    assertSame(軫, 軫.next(2800))
    assertSame(軫, 軫.prev(2800))
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
      assertTrue(station.animal.toString(Locale.TAIWAN).length == 1)
      assertTrue(station.animal.toString(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.animal.toString(Locale.ENGLISH))
    }
  }
}
