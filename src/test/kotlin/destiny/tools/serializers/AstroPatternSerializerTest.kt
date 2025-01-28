/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.*
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class AstroPatternSerializerTest {

  private val logger = KotlinLogging.logger {}

  @Nested
  inner class GrandTrineSerializerTest {
    @Test
    fun withScore() {
      val pattern = AstroPattern.GrandTrine(setOf(Planet.SUN, Planet.JUPITER, Planet.VENUS), Element.WATER, 0.95.toScore())
      Json.encodeToString(AstroPattern.GrandTrine.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(
            Planet.SUN.nameKey, Planet.JUPITER.nameKey, Planet.VENUS.nameKey,
          ),
          setOf(
            docCtx.read("$.points[0]", String::class.java),
            docCtx.read("$.points[1]", String::class.java),
            docCtx.read("$.points[2]", String::class.java),
          )
        )

        assertEquals(Element.WATER.name, docCtx.read("$.element"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.GrandTrine.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.GrandTrine(setOf(Planet.SUN, Planet.JUPITER, Planet.VENUS), Element.WATER, null)
      Json.encodeToString(AstroPattern.GrandTrine.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.GrandTrine.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class KiteSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Kite(
        PointSignHouse(Planet.SUN , ZodiacSign.LEO, 1),
        setOf(
          Planet.VENUS,
          Planet.JUPITER,
        ),
        PointSignHouse(Planet.MARS, ZodiacSign.AQUARIUS, 7),
        0.95.toScore()
      )
      Json.encodeToString(AstroPattern.Kite.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(Planet.SUN.nameKey, docCtx.read("$.head.point"))
        assertEquals(ZodiacSign.LEO.name, docCtx.read("$.head.sign"))
        assertEquals(1, docCtx.read("$.head.house"))

        assertEquals(
          setOf(
            Planet.VENUS.nameKey ,Planet.JUPITER.nameKey
          ),
          setOf(
            docCtx.read("$.wings[0]"),
            docCtx.read("$.wings[1]"),
          )
        )

        assertEquals(Planet.MARS.nameKey, docCtx.read("$.tail.point"))
        assertEquals(ZodiacSign.AQUARIUS.name, docCtx.read("$.tail.sign"))
        assertEquals(7, docCtx.read("$.tail.house"))
        assertEquals(0.95 ,docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.Kite.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Kite(
        PointSignHouse(Planet.SUN, ZodiacSign.LEO, 1),
        setOf(
          Planet.VENUS,
          Planet.JUPITER,
        ),
        PointSignHouse(Planet.MARS, ZodiacSign.AQUARIUS, 7),
        null
      )
      Json.encodeToString(AstroPattern.Kite.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Kite.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class TSquaredSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.TSquared(
        setOf(Planet.SUN, Planet.JUPITER),
        PointSignHouse(Planet.VENUS, ZodiacSign.LEO, 1),
        0.95.toScore()
      )
      Json.encodeToString(AstroPattern.TSquared.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(Planet.SUN.nameKey, Planet.JUPITER.nameKey),
          setOf(docCtx.read("$.oppoPoints[0]"), docCtx.read("$.oppoPoints[1]"))
        )
        assertEquals(Planet.VENUS.nameKey, docCtx.read("$.squared.point"))
        assertEquals(ZodiacSign.LEO.name, docCtx.read("$.squared.sign"))
        assertEquals(1, docCtx.read("$.squared.house"))
        assertEquals(0.95 , docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.TSquared.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.TSquared(
        setOf(
          Planet.SUN, Planet.JUPITER
        ),
        PointSignHouse(Planet.VENUS, ZodiacSign.LEO, 1),
        null
      )
      Json.encodeToString(AstroPattern.TSquared.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.TSquared.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }
}
