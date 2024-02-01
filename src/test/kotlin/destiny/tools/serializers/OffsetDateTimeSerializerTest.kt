/**
 * Created by smallufo on 2024-02-02.
 */
package destiny.tools.serializers

import kotlinx.serialization.json.Json
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class OffsetDateTimeSerializerTest {

  @Test
  fun testSerialization() {

    val offsetDateTimeUTC = OffsetDateTime.of(2022, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC)
    val offsetDateTimeGMT8 = OffsetDateTime.of(2022, 1, 1, 20, 0, 0, 0, ZoneOffset.ofHours(8))

    val serializedUTC = Json.encodeToString(OffsetDateTimeSerializer(), offsetDateTimeUTC)
    val serializedGMT8 = Json.encodeToString(OffsetDateTimeSerializer(), offsetDateTimeGMT8)

    assertEquals("\"2022-01-01T12:00Z\"", serializedUTC)
    assertEquals("\"2022-01-01T20:00+08:00\"", serializedGMT8)
  }

  @Test
  fun testDeserialization() {
    val jsonStringUTC = "\"2022-01-01T12:00Z\""
    val jsonStringGMT8 = "\"2022-01-01T20:00+08:00\""

    val deserializedUTC = Json.decodeFromString(OffsetDateTimeSerializer(), jsonStringUTC)
    val deserializedGMT8 = Json.decodeFromString(OffsetDateTimeSerializer(), jsonStringGMT8)

    val expectedUTC = OffsetDateTime.of(2022, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC)
    val expectedGMT8 = OffsetDateTime.of(2022, 1, 1, 20, 0, 0, 0, ZoneOffset.ofHours(8))

    assertEquals(expectedUTC, deserializedUTC)
    assertEquals(expectedGMT8, deserializedGMT8)
  }
}
