/**
 * Created by smallufo on 2024-09-12.
 */
package destiny.core.astrology

import destiny.core.Circular
import destiny.core.Graph
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign.*
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.tools.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyzerTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun venus_should_isolated() {
    val planetSignMap = mapOf(
      SUN to VIRGO,
      MOON to SAGITTARIUS,
      MERCURY to VIRGO,
      VENUS to LIBRA,
      MARS to CANCER,
      JUPITER to GEMINI,
      SATURN to PISCES,
    )
    verifyMap(planetSignMap) { graphResult ->
      assertTrue(graphResult.circles.isEmpty())

      assertEquals(3, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(SUN, MERCURY),
          listOf(MARS, MOON, JUPITER, MERCURY),
          listOf(SATURN, JUPITER, MERCURY),
        ),
        graphResult.paths
      )
      assertEquals(setOf(VENUS), graphResult.isolated)
      assertEquals(setOf(MERCURY), graphResult.terminals)
    }
  }

  @Test
  fun all_isolated() {
    val planetSignMap = mapOf(
      SUN to LEO,
      MOON to CANCER,
      MERCURY to GEMINI,
      VENUS to LIBRA,
      MARS to SCORPIO,
      JUPITER to SAGITTARIUS,
      SATURN to CAPRICORN,
    )
    verifyMap(planetSignMap) { graphResult ->
      assertTrue(graphResult.circles.isEmpty())
      assertTrue(graphResult.paths.isEmpty())
      assertEquals(setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN), graphResult.isolated)
      assertTrue(graphResult.terminals.isEmpty())
    }
  }

  /**
   * 一個 circle 四顆星
   * 各有三顆星分別指向此 circle裡面的三顆星
   */
  @Test
  fun three_paths_point_to_one_circle() {

    val planetSignMap = mapOf(
      SUN to CAPRICORN,
      MOON to SCORPIO,
      MERCURY to CAPRICORN,
      VENUS to SAGITTARIUS,
      MARS to AQUARIUS,
      JUPITER to ARIES,
      SATURN to TAURUS,
    )

    verifyMap(planetSignMap) { graphResult ->
      assertEquals(1, graphResult.circles.size)
      val expectedCircle = Circular.of(SATURN, VENUS, JUPITER, MARS)
      assertEquals(setOf(expectedCircle), graphResult.circles)

      assertEquals(3, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(SUN, SATURN),
          listOf(MOON, MARS),
          listOf(MERCURY, SATURN),
        ),
        graphResult.paths
      )
      assertTrue { graphResult.isolated.isEmpty() }
      assertTrue { graphResult.terminals.isEmpty() }
    }
  }

  /**
   * 一個 circle 三顆星
   * 其他四顆星, 形成兩條 path , 全部指向 circle 內的月亮 , 每個 path 長度為三顆星
   */
  @Test
  fun four_paths_point_to_moon_in_circle() {
    val planetSignMap = mapOf(
      SUN to CANCER,
      MOON to SAGITTARIUS,
      MERCURY to CANCER,
      VENUS to CANCER,
      MARS to VIRGO,
      JUPITER to TAURUS,
      SATURN to LEO
    )
    verifyMap(planetSignMap) { graphResult ->
      assertEquals(1, graphResult.circles.size)
      assertEquals(setOf(Circular.of(MOON, JUPITER, VENUS)), graphResult.circles)

      assertEquals(2, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(MARS, MERCURY, MOON),
          listOf(SATURN, SUN, MOON)
        ),
        graphResult.paths
      )
      assertTrue { graphResult.isolated.isEmpty() }
      assertTrue { graphResult.terminals.isEmpty() }
    }
  }

  @Test
  fun one_path() {
    val planetSignMap = mapOf(
      SUN to CANCER,
      MOON to GEMINI,
      MERCURY to TAURUS,
      VENUS to ARIES,
      MARS to SAGITTARIUS,
      JUPITER to CAPRICORN,
      SATURN to CAPRICORN
    )
    verifyMap(planetSignMap) { graphResult ->
      assertTrue { graphResult.circles.isEmpty() }
      assertTrue { graphResult.isolated.isEmpty() }
      assertEquals(1, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
        ),
        graphResult.paths
      )
      assertEquals(setOf(SATURN), graphResult.terminals)
    }
  }

  @Test
  fun two_paths() {
    val planetSignMap = mapOf(
      // path1 1
      SUN to CANCER,
      MOON to GEMINI,
      MERCURY to TAURUS,
      VENUS to TAURUS,
      // circle 2
      MARS to SAGITTARIUS,
      JUPITER to CAPRICORN,
      SATURN to CAPRICORN
    )
    verifyMap(planetSignMap) { graphResult ->
      assertTrue { graphResult.circles.isEmpty() }
      assertEquals(2, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(SUN, MOON, MERCURY, VENUS),
          listOf(MARS, JUPITER, SATURN)
        ), graphResult.paths
      )
      assertTrue { graphResult.isolated.isEmpty() }
      assertEquals(setOf(VENUS, SATURN), graphResult.terminals)
    }
  }

  /**
   * 星盤中呈現三條路徑，全部都集中到水星
   */
  @Test
  fun all_paths_point_to_one_planet() {
    val planetSignMap = mapOf(
      // path1 1
      SUN to CANCER,
      MOON to GEMINI,
      MERCURY to GEMINI,
      // path 2
      VENUS to ARIES,
      MARS to GEMINI,
      // path 3
      JUPITER to CAPRICORN,
      SATURN to GEMINI
    )
    verifyMap(planetSignMap) { graphResult ->
      assertTrue { graphResult.circles.isEmpty() }
      assertEquals(3, graphResult.paths.size)
      assertEquals(
        setOf(
          listOf(SUN, MOON, MERCURY),
          listOf(VENUS, MARS, MERCURY),
          listOf(JUPITER, SATURN, MERCURY)
        ), graphResult.paths
      )
      assertTrue { graphResult.isolated.isEmpty() }
      assertEquals(setOf(MERCURY), graphResult.terminals)
    }
  }

  @Test
  fun one_circle() {
    val planetSignMap = mapOf(
      SUN to CANCER,
      MOON to GEMINI,
      MERCURY to TAURUS,
      VENUS to ARIES,
      MARS to SAGITTARIUS,
      JUPITER to CAPRICORN,
      SATURN to LEO
    )
    verifyMap(planetSignMap) { graphResult ->
      assertEquals(1, graphResult.circles.size)
      assertEquals(
        setOf(
          Circular.of(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
        ),
        graphResult.circles
      )
      assertTrue { graphResult.paths.isEmpty() }
      assertTrue { graphResult.isolated.isEmpty() }
      assertTrue { graphResult.terminals.isEmpty() }
    }
  }

  @Test
  fun two_circles() {
    val planetSignMap = mapOf(
      // circle 1
      SUN to CANCER,
      MOON to GEMINI,
      MERCURY to TAURUS,
      VENUS to LEO,
      // circle 2
      MARS to SAGITTARIUS,
      JUPITER to CAPRICORN,
      SATURN to ARIES
    )
    verifyMap(planetSignMap) { graphResult ->
      assertEquals(2, graphResult.circles.size)
      assertEquals(
        setOf(
          Circular.of(SUN, MOON, MERCURY, VENUS),
          Circular.of(MARS, JUPITER, SATURN)
        ), graphResult.circles
      )
      assertTrue { graphResult.paths.isEmpty() }
      assertTrue { graphResult.isolated.isEmpty() }
      assertTrue { graphResult.terminals.isEmpty() }
    }
  }

  private val rulerMap: Map<ZodiacSign, Planet> = ZodiacSign.entries.associateWith { sign ->
    with(RulerPtolemyImpl) {
      sign.getRulerPoint(null)
    }!!
  }

  private fun verifyMap(planetSignMap: Map<Planet, ZodiacSign>, assertions: (r: Graph<Planet>) -> Unit) {
    planetSignMap.forEach { (planet, sign) -> logger.info { "$planet in $sign" } }
    Analyzer.analyzeHoroscope(planetSignMap, rulerMap).also { graph: Graph<Planet> ->
      logger.info { "result = $graph" }
      assertions.invoke(graph)
    }
  }
}
