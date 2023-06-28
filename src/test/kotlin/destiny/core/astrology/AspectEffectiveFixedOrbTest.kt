/**
 * Created by smallufo on 2022-08-04.
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AspectEffectiveFixedOrbTest {

  data class Data(val p1Deg: Double, val p2Deg: Double, val aspect: Aspect, val error: Double, val score: Double?)

  // if orb = 3.0
  private fun testData() = Stream.of(

    Data(0.0, 0.0, Aspect.CONJUNCTION, 0.0, 1.0),
    Data(0.0, 1.5, Aspect.CONJUNCTION, 1.5, 0.8),
    Data(0.0, 3.0, Aspect.CONJUNCTION, 3.0, 0.6),
    Data(0.0, 4.0, Aspect.CONJUNCTION, 4.0, null),
    Data(0.0, 357.0, Aspect.CONJUNCTION, 3.0, 0.6),

    Data(90.0, 0.0, Aspect.SQUARE, 0.0, 1.0),
    Data(90.0, 1.5, Aspect.SQUARE, 1.5, 0.8),
    Data(90.0, 3.0, Aspect.SQUARE, 3.0, 0.6),
    Data(90.0, 4.0, Aspect.SQUARE, 4.0, null),
    Data(90.0, 357.0, Aspect.SQUARE, 3.0, 0.6),

    Data(60.0, 0.0, Aspect.SEXTILE, 0.0, 1.0),
    Data(60.0, 1.5, Aspect.SEXTILE, 1.5, 0.8),
    Data(60.0, 3.0, Aspect.SEXTILE, 3.0, 0.6),
    Data(60.0, 4.0, Aspect.SEXTILE, 4.0, null),
    Data(60.0, 357.0, Aspect.SEXTILE, 3.0, 0.6),

    Data(180.0, 0.0, Aspect.OPPOSITION, 0.0, 1.0),
    Data(180.0, 1.5, Aspect.OPPOSITION, 1.5, 0.8),
    Data(180.0, 3.0, Aspect.OPPOSITION, 3.0, 0.6),
    Data(180.0, 4.0, Aspect.OPPOSITION, 4.0, null),
    Data(180.0, 357.0, Aspect.OPPOSITION, 3.0, 0.6),
  )

  @ParameterizedTest
  @MethodSource("testData")
  fun testEffectiveErrorAndScore(data: Data) {

    val impl = AspectEffectiveFixedOrb(3.0)
    impl.getEffectiveErrorAndScore(Planet.SUN, data.p1Deg.toZodiacDegree(), Planet.MOON, data.p2Deg.toZodiacDegree(), data.aspect).let { pair ->
      if (pair == null) {
        assertNull(data.score)
      } else {
        val (error, score) = pair
        assertEquals(data.error, error)
        assertEquals(data.score, score)
      }
    }
  }
}
