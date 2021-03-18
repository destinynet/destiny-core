/**
 * Created by smallufo on 2021-03-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
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
    Triple(SUN, 巳, LunarStation.房),

    Triple(MOON, 子, LunarStation.虛),
    Triple(MOON, 未, LunarStation.張),

    Triple(MARS, 酉, LunarStation.觜),
    Triple(MARS, 寅, LunarStation.室),

    Triple(MERCURY, 辰, LunarStation.箕),
    Triple(MERCURY, 亥, LunarStation.壁),

    Triple(JUPITER, 午, LunarStation.角),

    Triple(VENUS, 丑, LunarStation.斗),
    Triple(VENUS, 申, LunarStation.鬼),

    Triple(SATURN, 卯, LunarStation.女),
    Triple(SATURN, 戌, LunarStation.胃)
  )

  @ParameterizedTest
  @MethodSource
  fun hiddenVenusFoe(triple: Triple<Planet, Branch, LunarStation>) {

    val (yearPlanet, dayBranch, dayStation) = triple
    assertTrue(IHiddenVenusFoe.isDayFoeForYear(yearPlanet, dayBranch, dayStation))
  }
}
