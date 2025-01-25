/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class IZodiacDegreeSerializerTest {

  val logger = KotlinLogging.logger {}

  @Test
  fun testSerialize() {
    val zDeg = ZodiacDegree.of(ZodiacSign.ARIES, 20.0)
    Json.encodeToString(IZodiacDegreeSerializer, zDeg).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertEquals(ZodiacSign.ARIES.name, docCtx.read("$.sign"))
      assertEquals(20.0, docCtx.read("$.degree"))
      Json.decodeFromString(IZodiacDegreeSerializer, rawJson).also { parsed ->
        assertEquals(zDeg, parsed)
      }
    }
  }
}
