/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.ITimeLoc
import destiny.core.TimeLoc
import destiny.core.calendar.locationOf
import destiny.tools.serializers.Assertions.assertTimeLocEquals
import kotlinx.serialization.json.Json
import destiny.tools.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class ITimeLocSerializerTest {

  private val logger = KotlinLogging.logger { }

  private val json = Json {
//    serializersModule = SerializersModule {
//      contextual(LocalDateTime::class, LocalDateTimeSerializer)
//    }
    encodeDefaults = false
  }

  @Test
  fun testSerializeDeserialize0() {
    val time = LocalDateTime.of(2023, 10, 24, 1, 50)
    val loc = locationOf(Locale.TAIWAN)
    val timeLoc = TimeLoc(time, loc)

    val serialized = Json.encodeToString(ITimeLocSerializer, timeLoc)
    logger.info { serialized }
    val deserialized = Json.decodeFromString(ITimeLocSerializer, serialized)

    assertTimeLocEquals(timeLoc , deserialized)
  }

  @Test
  fun testSerializeDeserialize() {
    val time = LocalDateTime.of(2023, 10, 24, 1, 50)
    val loc = locationOf(Locale.TAIWAN)
    val timeLoc: ITimeLoc = TimeLoc(time, loc)

    val serialized = json.encodeToString(ITimeLocSerializer, timeLoc)
    logger.info { serialized }
    val deserialized = json.decodeFromString(ITimeLocSerializer, serialized)

    assertTimeLocEquals(timeLoc, deserialized)
  }

}
