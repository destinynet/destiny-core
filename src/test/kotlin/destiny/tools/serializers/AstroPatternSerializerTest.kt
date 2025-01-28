/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.AstroPattern
import destiny.core.astrology.Element
import destiny.core.astrology.Planet.*
import destiny.core.astrology.PointSignHouse
import destiny.core.astrology.Quality
import destiny.core.astrology.ZodiacSign.*
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import kotlin.test.*


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
        assertEquals(0.95, docCtx.read("$.score"))

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
        assertEquals(0.95, docCtx.read("$.score"))

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
          setOf(VENUS.nameKey, JUPITER.nameKey),
          setOf(docCtx.read("$.bottoms[0]"), docCtx.read("$.bottoms[1]"))
        )
        assertEquals(MARS.nameKey, docCtx.read("$.pointer.point"))
        assertEquals(LEO.name, docCtx.read("$.pointer.sign"))
        assertEquals(1, docCtx.read("$.pointer.house"))
        assertEquals(0.95, docCtx.read("$.score"))

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

  @Nested
  inner class BoomerangSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Boomerang(
        AstroPattern.Yod(
          setOf(VENUS, JUPITER),
          PointSignHouse(MARS, LEO, 1), 0.95.toScore()
        ),
        PointSignHouse(SATURN, AQUARIUS, 7), 0.85.toScore()
      )
      Json.encodeToString(AstroPattern.Boomerang.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNotNull(docCtx.read("$.yod"))
        assertEquals(SATURN.nameKey, docCtx.read("$.oppoPoint.point"))
        assertEquals(AQUARIUS.name, docCtx.read("$.oppoPoint.sign"))
        assertEquals(7, docCtx.read("$.oppoPoint.house"))

        assertEquals(0.85, docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.Boomerang.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Boomerang(
        AstroPattern.Yod(
          setOf(VENUS, JUPITER),
          PointSignHouse(MARS, LEO, 1), 0.95.toScore()
        ),
        PointSignHouse(SATURN, AQUARIUS, 7), null
      )
      Json.encodeToString(AstroPattern.Boomerang.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.Boomerang.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class GoldenYodSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.GoldenYod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), 0.95.toScore()
      )
      Json.encodeToString(AstroPattern.GoldenYod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(VENUS.nameKey, JUPITER.nameKey),
          setOf(docCtx.read("$.bottoms[0]"), docCtx.read("$.bottoms[1]"))
        )
        assertEquals(MARS.nameKey, docCtx.read("$.pointer.point"))
        assertEquals(LEO.name, docCtx.read("$.pointer.sign"))
        assertEquals(1, docCtx.read("$.pointer.house"))
        assertEquals(0.95, docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.GoldenYod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.GoldenYod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), null
      )
      Json.encodeToString(AstroPattern.GoldenYod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.GoldenYod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

  }

  @Nested
  inner class GrandCrossSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.GrandCross(setOf(VENUS, JUPITER, SATURN, MARS), Quality.FIXED, 0.95.toScore())
      Json.encodeToString(AstroPattern.GrandCross.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(VENUS.nameKey, MARS.nameKey, JUPITER.nameKey, SATURN.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"))
        )
        assertSame(Quality.FIXED, Quality.valueOf(docCtx.read("$.quality")))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.GrandCross.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.GrandCross(setOf(VENUS, JUPITER, SATURN, MARS), Quality.FIXED, null)
      Json.encodeToString(AstroPattern.GrandCross.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.GrandCross.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class DoubleTSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.DoubleT(
        setOf(
          AstroPattern.TSquared(
            setOf(SUN, JUPITER),
            PointSignHouse(VENUS, LEO, 1),
            0.8.toScore()
          ),
          AstroPattern.TSquared(
            setOf(MOON, MERCURY),
            PointSignHouse(MARS, CAPRICORN, 6),
            0.9.toScore()
          )
        ), 0.95.toScore()
      )
      Json.encodeToString(AstroPattern.DoubleT.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(2, docCtx.read("$.tSquares.length()"))
        assertEquals(0.95, docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.DoubleT.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.DoubleT(
        setOf(
          AstroPattern.TSquared(
            setOf(SUN, JUPITER),
            PointSignHouse(VENUS, LEO, 1),
            0.8.toScore()
          ),
          AstroPattern.TSquared(
            setOf(MOON, MERCURY),
            PointSignHouse(MARS, CAPRICORN, 6),
            0.9.toScore()
          )
        ), null
      )
      Json.encodeToString(AstroPattern.DoubleT.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(AstroPattern.DoubleT.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class HexagonSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Hexagon(
        setOf(
          AstroPattern.GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.91.toScore()),
          AstroPattern.GrandTrine(setOf(MERCURY, MARS, SATURN), Element.FIRE, 0.92.toScore()),
        ), 0.95.toScore()
      )
      Json.encodeToString(AstroPattern.Hexagon.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(2, docCtx.read("$.grandTrines.length()"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Hexagon.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Hexagon(
        setOf(
          AstroPattern.GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.91.toScore()),
          AstroPattern.GrandTrine(setOf(MERCURY, MARS, SATURN), Element.FIRE, 0.92.toScore()),
        ), null
      )
      Json.encodeToString(AstroPattern.Hexagon.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Hexagon.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class WedgeSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Wedge(setOf(MARS , SATURN), PointSignHouse(JUPITER, LEO, 1), 0.95.toScore())
      Json.encodeToString(AstroPattern.Wedge.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(MARS.nameKey ,SATURN.nameKey),
          setOf(docCtx.read("$.oppoPoints[0]"), docCtx.read("$.oppoPoints[1]"))
        )

        assertEquals(JUPITER.nameKey, docCtx.read("$.moderator.point"))
        assertEquals(LEO.name, docCtx.read("$.moderator.sign"))
        assertEquals(1, docCtx.read("$.moderator.house"))

        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Wedge.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Wedge(setOf(MARS, SATURN), PointSignHouse(JUPITER, LEO, 1), null)
      Json.encodeToString(AstroPattern.Wedge.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Wedge.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class MysticRectangleSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.MysticRectangle(setOf(SUN, MERCURY, VENUS, MARS), 0.95.toScore())
      Json.encodeToString(AstroPattern.MysticRectangle.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"))
        )

        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.MysticRectangle.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.MysticRectangle(setOf(SUN, MERCURY, VENUS, MARS), null)
      Json.encodeToString(AstroPattern.MysticRectangle.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.MysticRectangle.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class PentagramSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.Pentagram(setOf(SUN, MERCURY, VENUS, MARS, MOON), 0.95.toScore())
      Json.encodeToString(AstroPattern.Pentagram.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )

        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Pentagram.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.Pentagram(setOf(SUN, MERCURY, VENUS, MARS, MOON), null)
      Json.encodeToString(AstroPattern.Pentagram.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.Pentagram.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class StelliumSignSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.StelliumSign(setOf(SUN, MERCURY, VENUS, MARS, MOON), LEO , 0.95.toScore())
      Json.encodeToString(AstroPattern.StelliumSign.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertEquals(LEO.name , docCtx.read("$.sign"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.StelliumSign.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.StelliumSign(setOf(SUN, MERCURY, VENUS, MARS, MOON), LEO, null)
      Json.encodeToString(AstroPattern.StelliumSign.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.StelliumSign.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class StelliumHouseSerializerTest {

    @Test
    fun withScore() {
      val pattern = AstroPattern.StelliumHouse(setOf(SUN, MERCURY, VENUS, MARS, MOON), 1, 0.95.toScore())
      Json.encodeToString(AstroPattern.StelliumHouse.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertEquals(1, docCtx.read("$.house"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.StelliumHouse.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = AstroPattern.StelliumHouse(setOf(SUN, MERCURY, VENUS, MARS, MOON), 1, null)
      Json.encodeToString(AstroPattern.StelliumHouse.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(AstroPattern.StelliumHouse.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }
}
