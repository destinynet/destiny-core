/**
 * Created by smallufo on 2022-03-12.
 */
package destiny.tools.serializers

import destiny.core.astrology.LunarStation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LunarStationSerializerTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testEncodeDecode() {
    val module = SerializersModule {
      contextual(LunarStationSerializer)
    }

    val format = Json { serializersModule = module }

    LunarStation.values.forEach { ls ->
      format.encodeToString(ls).also { s ->
        assertNotNull(s)
        assertEquals(""""${ls.nameKey}"""", s)
        assertEquals(ls, format.decodeFromString(s))
      }
    }


  }
}
