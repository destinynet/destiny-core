/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core.astrology

import destiny.core.AbstractPointTest
import destiny.core.astrology.LunarStation.*
import destiny.core.toString
import destiny.tools.getTitle
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.*

internal class LunarStationTest : AbstractPointTest(LunarStation::class) {

  @Test
  fun testSerialize() {
    Arabic.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }

  @Test
  fun testLoop() {
    assertEquals(0, 角.getAheadOf(角))
    assertEquals(1, 亢.getAheadOf(角))
    assertEquals(2, 氐.getAheadOf(角))
    assertEquals(27, 軫.getAheadOf(角))

    assertEquals(0, 軫.getAheadOf(軫))
    assertEquals(1, 角.getAheadOf(軫))
    assertEquals(2, 亢.getAheadOf(軫))
    assertEquals(27, 翼.getAheadOf(軫))

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
      assertTrue(station.animal.getTitle(Locale.TAIWAN).length == 1)
      assertTrue(station.animal.getTitle(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(station.animal.getTitle(Locale.ENGLISH))
    }
  }

}
