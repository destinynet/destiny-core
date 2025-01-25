/**
 * Created by smallufo on 2025-01-26.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.*
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class IMidPointWithFocalSerializerTest {

  private val logger = KotlinLogging.logger {}

  /**
   * {
   *    "midPoint":{
   *       "points":[
   *          "Planet.VENUS",
   *          "Planet.MERCURY"
   *       ],
   *       "zodiacDegree":{
   *          "sign":"ARIES",
   *          "degree":10.0
   *       },
   *       "house":2
   *    },
   *    "focal":"Planet.JUPITER",
   *    "orb":0.23
   * }
   */
  @Test
  fun testSerialize() {
    val midPoint = MidPoint(setOf(Planet.VENUS, Planet.MERCURY), ZodiacDegree.of(ZodiacSign.ARIES, 10.0), 2)
    val midPointWithFocal: IMidPointWithFocal = MidPointWithFocal(midPoint, Planet.JUPITER, 0.23)

    Json.encodeToString(IMidPointWithFocalSerializer, midPointWithFocal).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertEquals(Planet.JUPITER.nameKey, docCtx.read("$.focal"))
      assertEquals(0.23, docCtx.read("$.orb"))
    }
  }
}
