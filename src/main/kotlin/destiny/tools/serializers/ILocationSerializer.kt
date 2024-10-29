/**
 * Created by smallufo on 2023-10-23.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILocation
import destiny.core.calendar.LatValue
import destiny.core.calendar.LatValue.Companion.toLat
import destiny.core.calendar.LngValue
import destiny.core.calendar.LngValue.Companion.toLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object ILocationSerializer : KSerializer<ILocation> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ILocation") {
    element<Double>("lat")
    element<Double>("lng")
    element<String>("tzid", isOptional = true)
    element<Int>("minuteOffset", isOptional = true)
    element<Double>("altitudeMeter", isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: ILocation) {
    encoder.encodeStructure(descriptor) {
      encodeDoubleElement(descriptor, 0, value.lat.value)
      encodeDoubleElement(descriptor, 1, value.lng.value)
      value.tzid?.also { encodeStringElement(descriptor, 2, it) }
      value.minuteOffset?.also { encodeIntElement(descriptor, 3, it) }
      value.altitudeMeter?.also { encodeDoubleElement(descriptor, 4, it) }
    }
  }

  override fun deserialize(decoder: Decoder): ILocation {
    var lat = 0.0
    var lng = 0.0
    var tzid: String? = null
    var minuteOffset: Int? = null
    var altitudeMeter: Double? = null

    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> lat = decodeDoubleElement(descriptor, 0)
          1    -> lng = decodeDoubleElement(descriptor, 1)
          2    -> tzid = decodeStringElement(descriptor, 2)
          3    -> minuteOffset = decodeIntElement(descriptor, 3)
          4    -> altitudeMeter = decodeDoubleElement(descriptor, 4)
          else -> break@loop
        }
      }
    }

    return object : ILocation {
      override val lat: LatValue = lat.toLat()
      override val lng: LngValue = lng.toLng()
      override val tzid: String? = tzid
      override val minuteOffset: Int? = minuteOffset
      override val altitudeMeter: Double? = altitudeMeter
    }
  }

}
