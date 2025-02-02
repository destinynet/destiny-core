/**
 * Created by smallufo on 2025-02-03.
 */
package destiny.tools.serializers

import destiny.core.calendar.eightwords.EightWords
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class IEightWordsSerializerTest {
  private val logger = KotlinLogging.logger {}

  @Test
  fun testSerialize() {
    val ew = EightWords("甲子" , "乙丑" , "丙寅" , "丁卯")
    Json.encodeToString(IEightWordsSerializer, ew).also { rawJson ->
      logger.info { rawJson }
      Json.decodeFromString(IEightWordsSerializer, rawJson).also { parsed ->
        assertEquals(ew, parsed)
      }
    }
  }
}
