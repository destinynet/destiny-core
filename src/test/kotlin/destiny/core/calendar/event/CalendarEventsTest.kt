/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.calendar.event

import destiny.core.astrology.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.eclipse.LunarType
import destiny.core.astrology.eclipse.SolarType
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.SolarTerms
import destiny.tools.Lang
import kotlinx.serialization.json.Json
import kotlin.test.*

class CalendarEventsTest {

  /** 2026 年第三次水逆（UTC）：陰影期 10-04 → 停滯轉逆 10-24 天蠍 20.98° → 停滯轉順 11-13 天蠍 5.03° → 離開陰影 11-30 */
  private val mercury = RetrogradeCycleEvent(
    Planet.MERCURY,
    GmtJulDay(2461317.881), 215.03.toZodiacDegree(),
    GmtJulDay(2461337.800), 230.98.toZodiacDegree(),
    GmtJulDay(2461358.162), 215.03.toZodiacDegree(),
    GmtJulDay(2461374.762), 230.98.toZodiacDegree(),
  )

  @Test
  fun retrograde_anchorIsStationRetrograde() {
    assertEquals(mercury.retrograding, mercury.anchor)
    assertEquals(mercury.preparing, mercury.begin)
    assertEquals(mercury.leaving, mercury.end)
    assertEquals(CalendarEventType.Retrograde(Planet.MERCURY), mercury.type)
  }

  @Test
  fun retrograde_phaseAt() {
    assertNull(mercury.phaseAt(mercury.preparing - 1))
    assertEquals(RetrogradePhase.PREPARING, mercury.phaseAt(mercury.preparing))
    assertEquals(RetrogradePhase.RETROGRADING, mercury.phaseAt(mercury.retrograding))
    assertEquals(RetrogradePhase.LEAVING, mercury.phaseAt(mercury.returning))
    assertNull(mercury.phaseAt(mercury.leaving))
  }

  @Test
  fun retrograde_shadowZone() {
    assertTrue(mercury.inShadowZone(220.0.toZodiacDegree()))
    assertFalse(mercury.inShadowZone(200.0.toZodiacDegree()))
    assertFalse(mercury.inShadowZone(240.0.toZodiacDegree()))
  }

  /** 陰影區跨 0°：轉逆在牡羊 5°、轉順在雙魚 20° */
  @Test
  fun retrograde_shadowZone_acrossAries0() {
    val wrapped = mercury.copy(retrogradingLng = 5.0.toZodiacDegree(), returningLng = 350.0.toZodiacDegree())
    assertTrue(wrapped.inShadowZone(355.0.toZodiacDegree()))
    assertTrue(wrapped.inShadowZone(2.0.toZodiacDegree()))
    assertFalse(wrapped.inShadowZone(10.0.toZodiacDegree()))
    assertFalse(wrapped.inShadowZone(340.0.toZodiacDegree()))
  }

  @Test
  fun retrograde_ofCycle() {
    val cycle = RetrogradeCycle(
      Planet.MERCURY,
      mercury.preparing, Pos(215.03, 0.0),
      mercury.retrograding, Pos(230.98, 1.0),
      mercury.returning, Pos(215.03, -1.0),
      mercury.leaving, Pos(230.98, 0.0),
    )
    assertEquals(mercury, RetrogradeCycleEvent.of(cycle))
    assertEquals(cycle.anchor, RetrogradeCycleEvent.of(cycle).anchor)
  }

  @Test
  fun lunation_sunLngFollowsPhaseAngle() {
    val full = LunationEvent(LunarPhase.FULL, GmtJulDay(2461368.0), 62.0.toZodiacDegree())
    assertEquals(242.0, full.sunLng.value, 1e-9)
    val firstQuarter = LunationEvent(LunarPhase.FIRST_QUARTER, GmtJulDay(2461368.0), 30.0.toZodiacDegree())
    assertEquals(300.0, firstQuarter.sunLng.value, 1e-9)
  }

  /** 2026 冥王星往南出界（數值為示意） */
  private val pluto = OutOfBoundsEvent(
    Planet.PLUTO,
    GmtJulDay(2461263.5), 303.9.toZodiacDegree(),
    GmtJulDay(2461320.5), -23.93, 23.436, 303.4.toZodiacDegree(),
    GmtJulDay(2461384.5), 303.6.toZodiacDegree(),
  )

  @Test
  fun outOfBounds_anchorIsEntering() {
    assertEquals(pluto.begin, pluto.anchor)
    assertEquals(CalendarEventType.OutOfBounds(Planet.PLUTO), pluto.type)
    assertFalse(pluto.north)
    assertEquals(0.494, pluto.depth, 1e-9)
    assertEquals(pluto.begin, pluto.at(OutOfBoundsPhase.ENTERING))
    assertEquals(pluto.peak, pluto.at(OutOfBoundsPhase.PEAK))
    assertEquals(pluto.end, pluto.at(OutOfBoundsPhase.LEAVING))
    assertEquals("冥王星出界", pluto.getTitle(Lang.ZH_TW))
  }

  @Test
  fun everyEvent_jsonRoundTrip() {
    val events: List<ICalendarEvent> = listOf(
      mercury,
      SolarEclipseEvent(SolarType.TOTAL, GmtJulDay(2461265.15), GmtJulDay(2461265.24), GmtJulDay(2461265.33), 140.0.toZodiacDegree(), 65.2, -25.2),
      SolarEclipseEvent(SolarType.PARTIAL, GmtJulDay(2461265.15), GmtJulDay(2461265.24), GmtJulDay(2461265.33), 140.0.toZodiacDegree()),
      LunarEclipseEvent(LunarType.TOTAL, GmtJulDay(2461102.85), GmtJulDay(2461102.98), GmtJulDay(2461103.1), 162.0.toZodiacDegree()),
      LunationEvent(LunarPhase.NEW, GmtJulDay(2461350.5), 227.0.toZodiacDegree()),
      SolarTermEvent(SolarTerms.立冬, GmtJulDay(2461351.1), GmtJulDay(2461366.0)),
      VoidOfCourseEvent(Planet.MOON, VoidCourseImpl.Medieval, GmtJulDay(2461340.1), 28.0.toZodiacDegree(),
        GmtJulDay(2461340.4), 30.0.toZodiacDegree(), Aspect.TRINE, Planet.SATURN),
      pluto,
    )
    events.forEach { e ->
      val json = Json.encodeToString(ICalendarEvent.serializer(), e)
      // GmtJulDay 的序列化只保留到秒以下的精度，比對錨點容許 1 秒
      val back = Json.decodeFromString(ICalendarEvent.serializer(), json)
      assertEquals(e.type, back.type, json)
      assertEquals(e.anchor.value, back.anchor.value, 1.0 / 86400, json)
      assertEquals(e::class, back::class, json)
    }
  }
}
