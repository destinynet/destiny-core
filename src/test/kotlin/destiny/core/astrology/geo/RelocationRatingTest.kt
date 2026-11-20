/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.Planet
import destiny.tools.Score.Companion.toScore
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals

class RelocationRatingTest {

  @ParameterizedTest
  @CsvSource(
    "0.60, STRONGLY_FAVORABLE",
    "0.53, STRONGLY_FAVORABLE",
    "0.516, FAVORABLE",
    "0.505, NEUTRAL",
    "0.5, NEUTRAL",
    "0.495, NEUTRAL",
    "0.48, UNFAVORABLE",
    "0.40, STRONGLY_UNFAVORABLE",
  )
  fun of(score: Double, expected: RelocationRating) {
    assertEquals(expected, RelocationRating.of(score.toScore()))
  }

  @Test
  fun `理由只列 5 度以內、前三條`() {
    fun r(p: Planet, orb: Double, c: Double) = ScoreReason(p, GeoAngle.MC, orb, c, 0.0)
    val reasons = listOf(
      r(Planet.JUPITER, 1.0, 0.5),
      r(Planet.SATURN, 8.0, 0.9),
      r(Planet.VENUS, 4.9, 0.3),
      r(Planet.MARS, 2.0, 0.4),
      r(Planet.MOON, 3.0, 0.1),
    )
    assertEquals(listOf(Planet.JUPITER, Planet.MARS, Planet.VENUS), reasons.notable().map { it.point })
  }

  @Test
  fun `角距換成公里`() {
    assertEquals(111.2, ScoreReason(Planet.SUN, GeoAngle.ASC, 1.0, 1.0, 0.0).km, 0.1)
  }
}
