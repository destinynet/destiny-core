/**
 * Created by smallufo on 2025-01-26.
 */
package destiny.tools.serializers.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.MidPoint
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IMidPointSerializerTest {

  private val logger = KotlinLogging.logger {}

  /**
   * {
   *    "points":[
   *       "Planet.VENUS",
   *       "Planet.MERCURY"
   *    ],
   *    "zodiacDegree":{
   *       "sign":"ARIES",
   *       "degree":10.0
   *    },
   *    "house":2
   * }
   */
  @Test
  fun testSerialize() {
    val midPoint = MidPoint(setOf(Planet.VENUS, Planet.MERCURY), ZodiacDegree.of(ZodiacSign.ARIES, 10.0), 2)

    Json.encodeToString(IMidPointSerializer, midPoint).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertTrue { docCtx.read("$.points[0]", String::class.java).equals(Planet.VENUS.nameKey) || docCtx.read("$.points[1]", String::class.java).equals(Planet.VENUS.nameKey) }
      assertTrue { docCtx.read("$.points[0]", String::class.java).equals(Planet.MERCURY.nameKey) || docCtx.read("$.points[1]", String::class.java).equals(Planet.MERCURY.nameKey) }
      assertEquals(ZodiacSign.ARIES.name, docCtx.read("$.zodiacDegree.sign"))
      assertEquals(10.0, docCtx.read("$.zodiacDegree.degree"))
      assertEquals(2, docCtx.read("$.house"))
      Json.decodeFromString(IMidPointSerializer, rawJson).also { parsed ->
        assertEquals(midPoint, parsed)
      }
    }
  }
}
