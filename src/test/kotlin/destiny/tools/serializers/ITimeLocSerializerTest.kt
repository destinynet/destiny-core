/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.ITimeLoc
import destiny.core.TimeLoc
import destiny.core.calendar.Location
import destiny.core.calendar.locationOf
import destiny.tools.serializers.Assertions.assertTimeLocEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import destiny.tools.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

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

  /**
   * 1970-01-01 08:00 Asia/Taipei = 1970-01-01 00:00 UTC = epochSecond 0
   * 確保 epochSecond 使用出生地時區計算，而非 server 預設時區
   */
  @Test
  fun `taipei 1970-01-01 08_00 should have epochSecond 0`() {
    val time = LocalDateTime.of(1970, 1, 1, 8, 0)
    val loc = Location.of(25.03903, 121.517668, "Asia/Taipei")
    val timeLoc: ITimeLoc = TimeLoc(time, loc)

    val serialized = json.encodeToString(ITimeLocSerializer, timeLoc)
    logger.info { serialized }

    // 驗證 epochSecond = 0 (UTC 1970-01-01 00:00)
    val jsonObj = Json.parseToJsonElement(serialized).jsonObject
    val epochSecond = jsonObj["epochSecond"]!!.jsonPrimitive.long
    assertEquals(0L, epochSecond, "Taipei 1970-01-01 08:00 should be epoch 0")

    // 驗證 localDateTime 保留原始值
    val localDateTime = jsonObj["localDateTime"]!!.jsonPrimitive.content
    assertEquals("1970-01-01T08:00", localDateTime)

    // 驗證 roundtrip
    val deserialized = json.decodeFromString(ITimeLocSerializer, serialized)
    assertTimeLocEquals(timeLoc, deserialized)
  }

}
