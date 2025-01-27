/**
 * Created by smallufo on 2025-01-27.
 */
package destiny.core.astrology

import destiny.tools.KotlinLogging
import destiny.tools.serializers.StarSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class StarTest {

  private val logger = KotlinLogging.logger {}

  @Test
  fun testSerialize() {
    Star.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      logger.info { "$p = $rawJson" }
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(StarSerializer , rawJson))
    }
  }
}
