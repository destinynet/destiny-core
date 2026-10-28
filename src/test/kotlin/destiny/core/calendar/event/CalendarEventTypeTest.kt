/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.calendar.event

import destiny.core.astrology.LunarPhase
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.SolarTerms
import kotlinx.serialization.json.Json
import kotlin.test.*

class CalendarEventTypeTest {

  @Test
  fun code_roundTrips_forEveryDefault() {
    CalendarEventType.defaults.forEach { type ->
      assertEquals(type, CalendarEventType.ofCode(type.code), "code = ${type.code}")
    }
  }

  @Test
  fun defaults_coverEveryKind() {
    assertEquals(CalendarEventKind.entries.toSet(), CalendarEventType.defaults.map { it.kind }.toSet())
    // 8 逆行 + 2 食 + 4 月相 + 24 節氣 + 1 空亡 + 8 出界
    assertEquals(47, CalendarEventType.defaults.size)
  }

  @Test
  fun code_isStable() {
    assertEquals("RETROGRADE:Planet.MERCURY", CalendarEventType.Retrograde(Planet.MERCURY).code)
    assertEquals("SOLAR_ECLIPSE", CalendarEventType.SolarEclipse.code)
    assertEquals("LUNATION:FULL", CalendarEventType.Lunation(LunarPhase.FULL).code)
    assertEquals("SOLAR_TERM:立冬", CalendarEventType.SolarTerm(SolarTerms.立冬).code)
    assertEquals("VOID_OF_COURSE:Planet.MOON:Medieval", CalendarEventType.VoidOfCourse().code)
    assertEquals("OUT_OF_BOUNDS:Planet.PLUTO", CalendarEventType.OutOfBounds(Planet.PLUTO).code)
  }

  @Test
  fun voidOfCourse_schoolIsPartOfIdentity() {
    val medieval = CalendarEventType.VoidOfCourse(Planet.MOON, VoidCourseImpl.Medieval)
    val hellenistic = CalendarEventType.VoidOfCourse(Planet.MOON, VoidCourseImpl.Hellenistic)
    assertNotEquals(medieval, hellenistic)
    assertNotEquals(medieval.code, hellenistic.code)
    assertEquals(hellenistic, CalendarEventType.ofCode(hellenistic.code))
  }

  @Test
  fun ofCode_rejectsGarbage() {
    listOf(
      "", "FOO", "RETROGRADE", "RETROGRADE:Planet.XXX", "RETROGRADE:Planet.SUN",
      "SOLAR_ECLIPSE:extra", "LUNATION:HALF", "SOLAR_TERM:立冬:x", "VOID_OF_COURSE:Planet.MOON",
      "OUT_OF_BOUNDS", "OUT_OF_BOUNDS:Planet.SUN", "OUT_OF_BOUNDS:Planet.PLUTO:x"
    ).forEach { assertNull(CalendarEventType.ofCode(it), "'$it' should not parse") }
  }

  @Test
  fun sunAndMoon_doNotRetrograde() {
    assertFailsWith<IllegalArgumentException> { CalendarEventType.Retrograde(Planet.SUN) }
    assertFailsWith<IllegalArgumentException> { CalendarEventType.Retrograde(Planet.MOON) }
  }

  /** 太陽的赤緯就是邊界；月亮會出界，型別允許、預設不收 */
  @Test
  fun outOfBounds_sunRejected_moonAllowedButNotDefault() {
    assertFailsWith<IllegalArgumentException> { CalendarEventType.OutOfBounds(Planet.SUN) }
    val moon = CalendarEventType.OutOfBounds(Planet.MOON)
    assertEquals(moon, CalendarEventType.ofCode(moon.code))
    assertFalse(moon in CalendarEventType.defaults)
  }

  @Test
  fun config_serializationRoundTrip() {
    val config = CalendarEventConfig()
    val json = Json.encodeToString(CalendarEventConfig.serializer(), config)
    assertEquals(config, Json.decodeFromString(CalendarEventConfig.serializer(), json))
  }
}
