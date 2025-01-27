/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.core.astrology

import destiny.tools.KotlinLogging
import destiny.tools.serializers.AstroPointSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AstroPointTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testSerialize() {
    AstroPoint.values.forEach { p ->
      val rawJson = Json.encodeToString(AstroPointSerializer, p)
      logger.info { "$p = $rawJson" }
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }
}
