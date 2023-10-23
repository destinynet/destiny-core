/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import destiny.core.calendar.LocationPlace
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class ILocationPlaceSerializerTest {

  private val logger = KotlinLogging.logger { }
  private val json = Json { encodeDefaults = false }

  @Test
  fun testSerializeDeserialize() {
    val location = object : ILocation {
      override val lat: Double = 42.0
      override val lng: Double = -123.45
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
