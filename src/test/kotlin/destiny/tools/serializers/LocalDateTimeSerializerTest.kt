/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import destiny.tools.KotlinLogging
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDateTimeSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json { encodeDefaults = false }

  @Test
  fun minute() {
    val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50)
    val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
    logger.info { serialized }
    val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

    assertEquals(dateTime, deserialized)
  }

  @Test
  fun second() {
    val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50, 30)
    val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
    logger.info { serialized }
    val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

    assertEquals(dateTime, deserialized)
  }

  @Test
  fun nanoSecond() {
    val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50, 30, 123456789)
    val serialized = json.encodeToString(LocalDateTimeSerializer, dateTime)
    logger.info { serialized }
    val deserialized = json.decodeFromString(LocalDateTimeSerializer, serialized)

    assertEquals(dateTime, deserialized)
  }
}
