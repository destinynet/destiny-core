/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.AstroPattern
import destiny.core.astrology.Element
import destiny.core.astrology.Planet.*
import destiny.core.astrology.PointSignHouse
import destiny.core.astrology.ZodiacSign.AQUARIUS
import destiny.core.astrology.ZodiacSign.LEO
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
      val pattern = AstroPattern.GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.95.toScore())
      Json.encodeToString(AstroPattern.GrandTrine.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(
            SUN.nameKey, JUPITER.nameKey, VENUS.nameKey,
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
      val pattern = AstroPattern.GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, null)
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
        PointSignHouse(SUN, LEO, 1),
        setOf(
          VENUS,
          JUPITER,
        ),
        PointSignHouse(MARS, AQUARIUS, 7),
        0.95.toScore()
      )
      Json.encodeToString(AstroPattern.Kite.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(SUN.nameKey, docCtx.read("$.head.point"))
        assertEquals(LEO.name, docCtx.read("$.head.sign"))
        assertEquals(1, docCtx.read("$.head.house"))

        assertEquals(
          setOf(
            VENUS.nameKey, JUPITER.nameKey
          ),
          setOf(
            docCtx.read("$.wings[0]"),
            docCtx.read("$.wings[1]"),
          )
        )

        assertEquals(MARS.nameKey, docCtx.read("$.tail.point"))
        assertEquals(AQUARIUS.name, docCtx.read("$.tail.sign"))
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
        PointSignHouse(SUN, LEO, 1),
        setOf(
          VENUS,
          JUPITER,
        ),
        PointSignHouse(MARS, AQUARIUS, 7),
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
        setOf(SUN, JUPITER),
        PointSignHouse(VENUS, LEO, 1),
        0.95.toScore()
      )
      Json.encodeToString(AstroPattern.TSquared.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(SUN.nameKey, JUPITER.nameKey),
          setOf(docCtx.read("$.oppoPoints[0]"), docCtx.read("$.oppoPoints[1]"))
        )
        assertEquals(VENUS.nameKey, docCtx.read("$.squared.point"))
        assertEquals(LEO.name, docCtx.read("$.squared.sign"))
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
          SUN, JUPITER
        ),
        PointSignHouse(VENUS, LEO, 1),
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

  @Nested
  inner class YodSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Yod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), 0.95.toScore()
      )
      Json.encodeToString(AstroPattern.Yod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(VENUS.nameKey , JUPITER.nameKey),
          setOf(docCtx.read("$.bottoms[0]"), docCtx.read("$.bottoms[1]"))
        )
        assertEquals(MARS.nameKey , docCtx.read("$.pointer.point"))
        assertEquals(LEO.name, docCtx.read("$.pointer.sign"))
        assertEquals(1, docCtx.read("$.pointer.house"))
        assertEquals(0.95 , docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.Yod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Yod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), null
      )
      Json.encodeToString(AstroPattern.Yod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.Yod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }
}
