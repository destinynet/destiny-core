/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.AstroPattern
import destiny.core.astrology.Element
import destiny.core.astrology.Planet
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GrandTrineSerializerTest {

  private val logger = KotlinLogging.logger {}

  @Test
  fun testSerialize_withScore() {
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
  fun testSerialize_nullScore() {
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
