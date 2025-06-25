/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.Aspect
import destiny.core.astrology.IPointAspectPattern.AspectType
import destiny.core.astrology.Planet
import destiny.core.astrology.PointAspectPattern
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IPointAspectPatternSerializerTest {

  private val logger = KotlinLogging.logger {}

  @Test
  fun testSerialize_full() {

    val pattern = PointAspectPattern.of(Planet.JUPITER, Planet.VENUS, Aspect.TRINE, AspectType.APPLYING, 0.1, 0.99.toScore())

    Json.encodeToString(IPointAspectPatternSerializer, pattern).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertEquals(Planet.VENUS.nameKey, docCtx.read("$.points[0]"))
      assertEquals(Planet.JUPITER.nameKey, docCtx.read("$.points[1]"))
      assertEquals(120.0, docCtx.read("$.angle"))
      assertEquals(Aspect.TRINE.name, docCtx.read("$.aspect"))
      assertEquals(AspectType.APPLYING.name, docCtx.read("$.type"))
      assertEquals(0.1, docCtx.read("$.orb"))
      assertEquals(0.99, docCtx.read("$.score"))

      Json.decodeFromString(IPointAspectPatternSerializer, rawJson).also { parsed ->
        assertEquals(pattern, parsed)
      }
    }
  }

  @Test
  fun testSerialize_optionalFieldsAbsent() {
    val pattern = PointAspectPattern.of(Planet.JUPITER, Planet.VENUS, Aspect.TRINE, null, 0.1, null)
    Json.encodeToString(IPointAspectPatternSerializer, pattern).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertEquals(Planet.VENUS.nameKey, docCtx.read("$.points[0]"))
      assertEquals(Planet.JUPITER.nameKey, docCtx.read("$.points[1]"))
      assertEquals(120.0, docCtx.read("$.angle"))
      assertEquals(Aspect.TRINE.name, docCtx.read("$.aspect"))
      assertNull(docCtx.read("$.type"))
      assertEquals(0.1, docCtx.read("$.orb"))
      assertNull(docCtx.read("$.score"))

      Json.decodeFromString(IPointAspectPatternSerializer, rawJson).also { parsed ->
        assertEquals(pattern, parsed)
      }
    }
  }
}
