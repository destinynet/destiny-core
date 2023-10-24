/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.ITimeLoc
import destiny.core.TimeLoc
import destiny.core.calendar.locationOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ITimeLocSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
    serializersModule = SerializersModule {
      contextual(LocalDateTime::class, LocalDateTimeSerializer)
    }
    encodeDefaults = false
  }

  @Test
  fun testSerializeDeserialize0() {
    val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50)
    val loc = locationOf(Locale.TAIWAN)
    val timeLoc = TimeLoc(dateTime, loc)

    val serialized = Json.encodeToString(ITimeLocSerializer, timeLoc)
    logger.info { serialized }
    val deserialized = Json.decodeFromString(ITimeLocSerializer, serialized)

    assertEquals(timeLoc, deserialized)
  }

  @Test
  fun testSerializeDeserialize() {
    val dateTime = LocalDateTime.of(2023, 10, 24, 1, 50)
    val loc = locationOf(Locale.TAIWAN)
    val timeLoc: ITimeLoc = TimeLoc(dateTime, loc)

    val serialized = json.encodeToString(ITimeLocSerializer, timeLoc)
    logger.info { serialized }
    val deserialized = json.decodeFromString(ITimeLocSerializer, serialized)

    assertEquals(timeLoc, deserialized)
  }
}
