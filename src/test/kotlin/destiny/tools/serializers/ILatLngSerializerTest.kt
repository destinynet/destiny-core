/**
 * Created by smallufo on 2023-10-23.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILatLng
import destiny.core.calendar.LatValue
import destiny.core.calendar.LatValue.Companion.toLat
import destiny.core.calendar.LngValue
import destiny.core.calendar.LngValue.Companion.toLng
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ILatLngSerializerTest {
  private val logger = KotlinLogging.logger { }
  private val json = Json { encodeDefaults = false }

  @Test
  fun testSerializeDeserialize() {
    val latLng = object : ILatLng {
      override val lat: LatValue = 42.0.toLat()
      override val lng: LngValue = (-123.45).toLng()
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
