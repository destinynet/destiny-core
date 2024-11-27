/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.calendar.*
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.tools.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ILocationPlaceSerializerTest {

  private val logger = KotlinLogging.logger { }
  private val json = Json { encodeDefaults = false }

  @Test
  fun testSerializeDeserialize() {
    val location = object : ILocation {
      override val lat: Lat = 42.0.toLat()
      override val lng: Lng = (-123.45).toLng()
      override val tzid: String = "America/New_York"
      override val minuteOffset: Int = -300
      override val altitudeMeter: Double = 123.0
    }
    val locPlace = LocationPlace(location, "New York")

    val serialized = json.encodeToString(ILocationPlaceSerializer, locPlace)
    logger.info { serialized }
    val deserialized: ILocationPlace = json.decodeFromString(ILocationPlaceSerializer, serialized)

    assertEquals(location.lat, deserialized.lat)
    assertEquals(location.lng, deserialized.lng)
    assertEquals(location.tzid, deserialized.tzid)
    assertEquals(location.minuteOffset, deserialized.minuteOffset)
    assertEquals(location.altitudeMeter, deserialized.altitudeMeter)
    assertEquals("New York", deserialized.place)
  }
}
