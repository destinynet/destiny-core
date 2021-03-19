/**
 * Created by smallufo on 2021-03-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IHiddenVenusFoeTest {

  @Test
  fun testHiddenVenus() {
    assertEquals(setOf(巳), IHiddenVenusFoe.getYearHiddenVenus(SUN))
    assertEquals(setOf(子, 未), IHiddenVenusFoe.getYearHiddenVenus(MOON))
    assertEquals(setOf(寅, 酉), IHiddenVenusFoe.getYearHiddenVenus(MARS))
    assertEquals(setOf(辰, 亥), IHiddenVenusFoe.getYearHiddenVenus(MERCURY))
    assertEquals(setOf(午), IHiddenVenusFoe.getYearHiddenVenus(JUPITER))
    assertEquals(setOf(丑, 申), IHiddenVenusFoe.getYearHiddenVenus(VENUS))
    assertEquals(setOf(卯, 戌), IHiddenVenusFoe.getYearHiddenVenus(SATURN))
  }


  fun hiddenVenusFoe() = Stream.of(
    Triple(SUN, 巳, 房),

    Triple(MOON, 子, 虛),
    Triple(MOON, 未, 張),

    Triple(MARS, 酉, 觜),
    Triple(MARS, 寅, 室),

    Triple(MERCURY, 辰, 箕),
    Triple(MERCURY, 亥, 壁),

    Triple(JUPITER, 午, 角),

    Triple(VENUS, 丑, 斗),
    Triple(VENUS, 申, 鬼),

    Triple(SATURN, 卯, 女),
    Triple(SATURN, 戌, 胃)
  )

  @ParameterizedTest
  @MethodSource
  fun hiddenVenusFoe(triple: Triple<Planet, Branch, LunarStation>) {

    val (yearPlanet, dayBranch, dayStation) = triple
    assertTrue(IHiddenVenusFoe.isDayFoeForYear(yearPlanet, dayBranch, dayStation))
  }


  @Test
  fun dayFoeForDayAndHour() {
    val included = mapOf(
      子 to 虛,
      丑 to 斗,
      寅 to 室,
      卯 to 女,
      辰 to 箕,
      巳 to 房,
      午 to 角,
      未 to 張,
      申 to 鬼,
      酉 to 觜,
      戌 to 胃,
      亥 to 壁
    )

    val excluded = Branch.values().flatMap { b -> LunarStation.values.map { b to it } }
      .filter { (branch , station) -> included[branch] != station }
      .toList()


    included.forEach { (b,s) ->
      assertTrue(IHiddenVenusFoe.isDayFoeForDay(s, b))
      assertTrue(IHiddenVenusFoe.isHourFoeForHour(s, b))
    }

    excluded.forEach { (b,s) ->
      assertFalse(IHiddenVenusFoe.isDayFoeForDay(s, b))
      assertFalse(IHiddenVenusFoe.isHourFoeForHour(s, b))
    }
  }
}
