/**
 * Created by smallufo on 2023-10-23.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILocation
import destiny.core.calendar.Lat
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng
import destiny.core.calendar.Lng.Companion.toLng
import destiny.tools.KotlinLogging
import destiny.tools.serializers.Assertions.assertLocEquals
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ILocationSerializerTest {
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

    val serialized = json.encodeToString(ILocationSerializer, location)
    logger.info { serialized }
    val deserialized = json.decodeFromString(ILocationSerializer, serialized)

    assertLocEquals(location, deserialized)
  }

  @Test
  fun testSerializeDeserializeWithoutOptionalFields() {
    val location = object : ILocation {
      override val lat: Lat = 33.0.toLat()
      override val lng: Lng = (-122.0).toLng()
      override val tzid: String? = null
      override val minuteOffset: Int? = null
      override val altitudeMeter: Double? = null
    }

    val serialized = json.encodeToString(ILocationSerializer, location)
    logger.info { serialized }
    val deserialized = json.decodeFromString(ILocationSerializer, serialized)

    assertLocEquals(location, deserialized)
  }
}
