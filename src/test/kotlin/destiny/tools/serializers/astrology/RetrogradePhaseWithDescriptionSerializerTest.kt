/**
 * Created by smallufo on 2025-01-27.
 */
package destiny.tools.serializers.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.astrology.RetrogradePhase
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetrogradePhaseWithDescriptionSerializerTest {
  private val logger = KotlinLogging.logger {}

  @Test
  fun testEncodeNotNull() {
    Json.encodeToString(RetrogradePhaseWithDescriptionSerializer, RetrogradePhase.RETROGRADING).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertEquals(RetrogradePhase.RETROGRADING.name, docCtx.read("$.state"))
      assertEquals("In retrograde motion", docCtx.read("$.description"))
    }
  }

  @Test
  fun testEncodeNull() {
    Json.encodeToString(RetrogradePhaseWithDescriptionSerializer, null).also { rawJson ->
      logger.info { rawJson }
      val docCtx = JsonPath.parse(rawJson)
      assertNull(docCtx.read("$.state"))
      assertTrue { docCtx.read("$.description", String::class.java).contains("no retrograde influence") }
    }
  }
}
