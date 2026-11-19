/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.core.calendar.Location
import destiny.core.calendar.LocationPlace
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoTopicTest {

  /** 宮頭：第 n 宮在白羊起算的第 n 個星座（ASC 在白羊的整宮盤） */
  private val natal: IHoroscopeModel = mockk {
    every { getCuspDegree(any()) } answers { ZodiacDegree.of(ZodiacSign.entries[firstArg<Int>() - 1], 0.0) }
  }

  private val love = GeoTopicFocus(houses = listOf(5, 7), planets = listOf(VENUS))
  private val career = GeoTopicFocus(houses = listOf(10, 6), planets = listOf(SUN, SATURN, JUPITER))

  @Test
  fun testAngles() {
    assertEquals(setOf(GeoAngle.DESC), love.angles)
    assertEquals(setOf(GeoAngle.MC), career.angles)
    assertEquals(GeoAngle.entries.toSet(), GeoTopicFocus(listOf(1, 4, 7, 10), emptyList()).angles)
  }

  /** 5 宮獅子 → 太陽、7 宮天秤 → 金星 */
  @Test
  fun testStarsIncludeHouseRulers() {
    assertEquals(setOf<AstroPoint>(VENUS, SUN), love.stars(natal))
    // 10 宮摩羯 → 土星、6 宮處女 → 水星
    assertEquals(setOf<AstroPoint>(SUN, SATURN, JUPITER, MERCURY), career.stars(natal))
  }

  @Test
  fun testFocusedOnBoostsOnlyTopic() {
    val base = RelocationScoreConfig()
    val focused = base.focusedOn(love, natal)
    assertEquals(base.angleWeights[GeoAngle.DESC]!! * 1.5, focused.angleWeights[GeoAngle.DESC]!!, 1e-12)
    assertEquals(base.angleWeights[GeoAngle.MC], focused.angleWeights[GeoAngle.MC])
    assertEquals(base.starWeights[VENUS]!! * 1.5, focused.starWeights[VENUS]!!, 1e-12)
    assertEquals(base.starWeights[SUN]!! * 1.5, focused.starWeights[SUN]!!, 1e-12)
    assertEquals(base.starWeights[MARS], focused.starWeights[MARS])
    // 吉凶軸不動
    assertEquals(base.starValences, focused.starValences)
    assertEquals(base.orbDeg, focused.orbDeg)
  }

  /** 金星壓 DESC 的城市 vs 木星壓 MC 的城市：不分主題時木星城市勝，看愛情時金星城市勝 */
  @Test
  fun testFocusChangesRanking() {
    val scorer = RelocationScorerClassicImpl()
    val place = LocationPlace(Location(25.0.toLat(), 121.5.toLng()), "x")
    // 實際的候選每顆星都有一條最近的線；其餘的星放在 orb 外（不貢獻分數、但進分母）
    val far: Map<AstroPoint, LineDistance> = destiny.core.astrology.Planet.list.associateWith { LineDistance(GeoAngle.MC, 30.0, 3336.0) }
    fun candidate(p: AstroPoint, a: GeoAngle) =
      RelocationCandidate(place, emptyMap(), far + (p to LineDistance(a, 1.0, 111.0)), emptyList())
    val venusDesc = candidate(VENUS, GeoAngle.DESC)
    val jupiterMc = candidate(JUPITER, GeoAngle.MC)

    val base = RelocationScoreConfig()
    assertTrue(scorer.suitability(jupiterMc, base) > scorer.suitability(venusDesc, base))

    val focused = base.focusedOn(love, natal)
    assertTrue(scorer.suitability(venusDesc, focused) > scorer.suitability(jupiterMc, focused))
  }
}
