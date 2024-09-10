/**
 * Created by smallufo on 2023-10-23.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILatLng
import kotlinx.serialization.json.Json
import destiny.tools.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ILatLngSerializerTest {
  private val logger = KotlinLogging.logger { }
  private val json = Json { encodeDefaults = false }

  @Test
  fun testSerializeDeserialize() {
    val latLng = object : ILatLng {
      override val lat: Double = 42.0
      override val lng: Double = -123.45
    }

    val serialized: String = json.encodeToString(ILatLngSerializer, latLng)
    logger.info { serialized }
    val deserialized: ILatLng = json.decodeFromString(ILatLngSerializer, serialized)

    assertEquals(latLng.lat, deserialized.lat)
    assertEquals(latLng.lng, deserialized.lng)
  }

  @Test
  fun testInvalidLatitude() {
    val jsonStr = """{"lat": -100.0, "lng": 50.0}"""

    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(ILatLngSerializer, jsonStr)
    }
  }

  @Test
  fun testInvalidLongitude() {
    val jsonStr = """{"lat": 35.0, "lng": 190.0}"""

    assertFailsWith<IllegalArgumentException> {
      json.decodeFromString(ILatLngSerializer, jsonStr)
    }
  }
}
