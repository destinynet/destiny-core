/**
 * Created by smallufo on 2023-10-29.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import destiny.tools.KotlinLogging
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ZonedDateTimeSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json { encodeDefaults = false }

  @Test
  fun asiaTaipei() {
    val zdt = ZonedDateTime.of(LocalDateTime.of(2023, 10, 29, 21, 40), ZoneId.of("Asia/Taipei"))
    json.encodeToString(ZonedDateTimeSerializer, zdt).also { serialized ->
      logger.info { serialized }
      assertNotNull(serialized)
      val deserialized = json.decodeFromString(ZonedDateTimeSerializer, serialized)
      assertEquals(zdt, deserialized)
    }
  }

  @Test
  fun utc() {
    val zdt = ZonedDateTime.of(LocalDateTime.of(2023, 10, 29, 21, 40), ZoneId.of("UTC"))
    json.encodeToString(ZonedDateTimeSerializer, zdt).also { serialized ->
      logger.info { serialized }
      assertNotNull(serialized)
      val deserialized = json.decodeFromString(ZonedDateTimeSerializer, serialized)
      assertEquals(zdt, deserialized)
    }
  }

}
