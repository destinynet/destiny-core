/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.AstroPattern
import destiny.core.astrology.AstroPattern.*
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
      val pattern = GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.95.toScore())
      Json.encodeToString(GrandTrine.serializer(), pattern).also { rawJson ->
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
        Json.decodeFromString(GrandTrine.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, null)
      Json.encodeToString(GrandTrine.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(GrandTrine.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class KiteSerializerTest {

    @Test
    fun withScore() {
      val pattern = Kite(
        PointSignHouse(SUN, LEO, 1),
        setOf(
          VENUS,
          JUPITER,
        ),
        PointSignHouse(MARS, AQUARIUS, 7),
        0.95.toScore()
      )
      Json.encodeToString(Kite.serializer(), pattern).also { rawJson ->
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

        Json.decodeFromString(Kite.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Kite(
        PointSignHouse(SUN, LEO, 1),
        setOf(
          VENUS,
          JUPITER,
        ),
        PointSignHouse(MARS, AQUARIUS, 7),
        null
      )
      Json.encodeToString(Kite.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(Kite.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class TSquaredSerializerTest {

    @Test
    fun withScore() {
      val pattern = TSquared(
        setOf(SUN, JUPITER),
        PointSignHouse(VENUS, LEO, 1),
        0.95.toScore()
      )
      Json.encodeToString(TSquared.serializer(), pattern).also { rawJson ->
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

        Json.decodeFromString(TSquared.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = TSquared(
        setOf(
          SUN, JUPITER
        ),
        PointSignHouse(VENUS, LEO, 1),
        null
      )
      Json.encodeToString(TSquared.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(TSquared.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class YodSerializerTest {

    @Test
    fun withScore() {
      val pattern = Yod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), 0.95.toScore()
      )
      Json.encodeToString(Yod.serializer(), pattern).also { rawJson ->
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

        Json.decodeFromString(Yod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Yod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), null
      )
      Json.encodeToString(Yod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(Yod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class BoomerangSerializerTest {

    @Test
    fun withScore() {
      val pattern = Boomerang(
        Yod(
          setOf(VENUS, JUPITER),
          PointSignHouse(MARS, LEO, 1), 0.95.toScore()
        ),
        PointSignHouse(SATURN, AQUARIUS, 7), 0.85.toScore()
      )
      Json.encodeToString(Boomerang.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNotNull(docCtx.read("$.yod"))
        assertEquals(SATURN.nameKey, docCtx.read("$.oppoPoint.point"))
        assertEquals(AQUARIUS.name, docCtx.read("$.oppoPoint.sign"))
        assertEquals(7, docCtx.read("$.oppoPoint.house"))

        assertEquals(0.85, docCtx.read("$.score"))

        Json.decodeFromString(Boomerang.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Boomerang(
        Yod(
          setOf(VENUS, JUPITER),
          PointSignHouse(MARS, LEO, 1), 0.95.toScore()
        ),
        PointSignHouse(SATURN, AQUARIUS, 7), null
      )
      Json.encodeToString(Boomerang.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(Boomerang.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class GoldenYodSerializerTest {

    @Test
    fun withScore() {
      val pattern = GoldenYod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), 0.95.toScore()
      )
      Json.encodeToString(GoldenYod.serializer(), pattern).also { rawJson ->
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

        Json.decodeFromString(GoldenYod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = GoldenYod(
        setOf(VENUS, JUPITER),
        PointSignHouse(MARS, LEO, 1), null
      )
      Json.encodeToString(GoldenYod.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(GoldenYod.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

  }

  @Nested
  inner class GrandCrossSerializerTest {

    @Test
    fun withScore() {
      val pattern = GrandCross(setOf(VENUS, JUPITER, SATURN, MARS), Quality.FIXED, 0.95.toScore())
      Json.encodeToString(GrandCross.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(
          setOf(VENUS.nameKey, MARS.nameKey, JUPITER.nameKey, SATURN.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"))
        )
        assertSame(Quality.FIXED, Quality.valueOf(docCtx.read("$.quality")))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(GrandCross.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = GrandCross(setOf(VENUS, JUPITER, SATURN, MARS), Quality.FIXED, null)
      Json.encodeToString(GrandCross.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(GrandCross.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class DoubleTSerializerTest {

    @Test
    fun withScore() {
      val pattern = DoubleT(
        setOf(
          TSquared(
            setOf(SUN, JUPITER),
            PointSignHouse(VENUS, LEO, 1),
            0.8.toScore()
          ),
          TSquared(
            setOf(MOON, MERCURY),
            PointSignHouse(MARS, CAPRICORN, 6),
            0.9.toScore()
          )
        ), 0.95.toScore()
      )
      Json.encodeToString(DoubleT.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(2, docCtx.read("$.tSquares.length()"))
        assertEquals(0.95, docCtx.read("$.score"))

        Json.decodeFromString(DoubleT.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = DoubleT(
        setOf(
          TSquared(
            setOf(SUN, JUPITER),
            PointSignHouse(VENUS, LEO, 1),
            0.8.toScore()
          ),
          TSquared(
            setOf(MOON, MERCURY),
            PointSignHouse(MARS, CAPRICORN, 6),
            0.9.toScore()
          )
        ), null
      )
      Json.encodeToString(DoubleT.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(DoubleT.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class HexagonSerializerTest {

    @Test
    fun withScore() {
      val pattern = Hexagon(
        setOf(
          GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.91.toScore()),
          GrandTrine(setOf(MERCURY, MARS, SATURN), Element.FIRE, 0.92.toScore()),
        ), 0.95.toScore()
      )
      Json.encodeToString(Hexagon.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(2, docCtx.read("$.grandTrines.length()"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(Hexagon.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Hexagon(
        setOf(
          GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.91.toScore()),
          GrandTrine(setOf(MERCURY, MARS, SATURN), Element.FIRE, 0.92.toScore()),
        ), null
      )
      Json.encodeToString(Hexagon.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(Hexagon.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class WedgeSerializerTest {

    @Test
    fun withScore() {
      val pattern = Wedge(setOf(MARS, SATURN), PointSignHouse(JUPITER, LEO, 1), 0.95.toScore())
      Json.encodeToString(AstroPattern.Wedge.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(MARS.nameKey, SATURN.nameKey),
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
      val pattern = Wedge(setOf(MARS, SATURN), PointSignHouse(JUPITER, LEO, 1), null)
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
      val pattern = MysticRectangle(setOf(SUN, MERCURY, VENUS, MARS), 0.95.toScore())
      Json.encodeToString(MysticRectangle.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"))
        )

        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(MysticRectangle.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = MysticRectangle(setOf(SUN, MERCURY, VENUS, MARS), null)
      Json.encodeToString(MysticRectangle.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(MysticRectangle.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class PentagramSerializerTest {

    @Test
    fun withScore() {
      val pattern = Pentagram(setOf(SUN, MERCURY, VENUS, MARS, MOON), 0.95.toScore())
      Json.encodeToString(Pentagram.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )

        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(Pentagram.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Pentagram(setOf(SUN, MERCURY, VENUS, MARS, MOON), null)
      Json.encodeToString(Pentagram.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(Pentagram.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class StelliumSignSerializerTest {

    @Test
    fun withScore() {
      val pattern = StelliumSign(setOf(SUN, MERCURY, VENUS, MARS, MOON), LEO, 0.95.toScore())
      Json.encodeToString(StelliumSign.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertEquals(LEO.name, docCtx.read("$.sign"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(StelliumSign.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = StelliumSign(setOf(SUN, MERCURY, VENUS, MARS, MOON), LEO, null)
      Json.encodeToString(StelliumSign.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(StelliumSign.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class StelliumHouseSerializerTest {

    @Test
    fun withScore() {
      val pattern = StelliumHouse(setOf(SUN, MERCURY, VENUS, MARS, MOON), 1, 0.95.toScore())
      Json.encodeToString(StelliumHouse.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertEquals(1, docCtx.read("$.house"))
        assertEquals(0.95, docCtx.read("$.score"))
        Json.decodeFromString(StelliumHouse.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = StelliumHouse(setOf(SUN, MERCURY, VENUS, MARS, MOON), 1, null)
      Json.encodeToString(StelliumHouse.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(SUN.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey, MOON.nameKey),
          setOf(docCtx.read("$.points[0]"), docCtx.read("$.points[1]"), docCtx.read("$.points[2]"), docCtx.read("$.points[3]"), docCtx.read("$.points[4]"))
        )
        assertNull(docCtx.read("$.score"))
        Json.decodeFromString(StelliumHouse.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class ConfrontationSerializerTest {

    @Test
    fun withScore() {
      val pattern = Confrontation(
        setOf(
          setOf(SUN, MOON, MERCURY, VENUS, MARS),
          setOf(JUPITER, SATURN, URANUS, NEPTUNE, PLUTO)
        ), 0.95.toScore()
      )
      Json.encodeToString(Confrontation.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(2, docCtx.read("$.clusters.length()"))
        assertEquals(0.95, docCtx.read("$.score"))

        Json.decodeFromString(Confrontation.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }

    @Test
    fun nullScore() {
      val pattern = Confrontation(
        setOf(
          setOf(SUN, MOON, MERCURY, VENUS, MARS),
          setOf(JUPITER, SATURN, URANUS, NEPTUNE, PLUTO)
        )
      )
      Json.encodeToString(Confrontation.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)

        assertEquals(
          setOf(
            setOf(SUN.nameKey, MOON.nameKey, MERCURY.nameKey, VENUS.nameKey, MARS.nameKey),
            setOf(JUPITER.nameKey, SATURN.nameKey, URANUS.nameKey, NEPTUNE.nameKey, PLUTO.nameKey)
          ),
          setOf(
            setOf(
              docCtx.read("$.clusters[0][0]"),
              docCtx.read("$.clusters[0][1]"),
              docCtx.read("$.clusters[0][2]"),
              docCtx.read("$.clusters[0][3]"),
              docCtx.read("$.clusters[0][4]"),
            ),
            setOf(
              docCtx.read("$.clusters[1][0]"),
              docCtx.read("$.clusters[1][1]"),
              docCtx.read("$.clusters[1][2]"),
              docCtx.read("$.clusters[1][3]"),
              docCtx.read("$.clusters[1][4]"),
            )
          )
        )

        assertNull(docCtx.read("$.score"))

        Json.decodeFromString(Confrontation.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }

  @Nested
  inner class AstroPatternSerializerTest {

    @Test
    fun testSerialize() {
      val pattern = GrandTrine(setOf(SUN, JUPITER, VENUS), Element.WATER, 0.95.toScore())
      Json.encodeToString(AstroPattern.serializer(), pattern).also { rawJson ->
        logger.info { rawJson }
        val docCtx = JsonPath.parse(rawJson)
        assertEquals(GrandTrine::class.simpleName!!, docCtx.read("$.type"))
        assertNotNull(docCtx.read("$.content"))
        Json.decodeFromString(AstroPattern.serializer(), rawJson).also { parsed ->
          assertEquals(pattern, parsed)
        }
      }
    }
  }
}
