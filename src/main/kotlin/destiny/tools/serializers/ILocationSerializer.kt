/**
 * Created by smallufo on 2023-10-23.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILatLng
import destiny.core.calendar.ILocation
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
    element<ILatLng>("latLng")
    element<String>("tzid", isOptional = true)
    element<Int>("minuteOffset", isOptional = true)
    element<Double>("altitudeMeter", isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: ILocation) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ILatLngSerializer, value)
      value.tzid?.also { encodeStringElement(descriptor, 1, it) }
      value.minuteOffset?.also { encodeIntElement(descriptor, 2, it) }
      value.altitudeMeter?.also { encodeDoubleElement(descriptor, 3, it) }
    }
  }

  override fun deserialize(decoder: Decoder): ILocation {
    var latLng: ILatLng = object : ILatLng {
      override val lng: Double = 0.0
      override val lat: Double = 0.0
    }
    var tzid: String? = null
    var minuteOffset: Int? = null
    var altitudeMeter: Double? = null

    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> latLng = decodeSerializableElement(descriptor, 0, ILatLngSerializer)
          1    -> tzid = decodeStringElement(descriptor, 1)
          2    -> minuteOffset = decodeIntElement(descriptor, 2)
          3    -> altitudeMeter = decodeDoubleElement(descriptor, 3)
          else -> break@loop
        }
      }
    }

    return object : ILocation {
      override val lat: Double = latLng.lat
      override val lng: Double = latLng.lng
      override val tzid: String? = tzid
      override val minuteOffset: Int? = minuteOffset
      override val altitudeMeter: Double? = altitudeMeter
    }
  }

}
