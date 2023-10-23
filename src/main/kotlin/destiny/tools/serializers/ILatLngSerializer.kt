package destiny.tools.serializers

import destiny.core.calendar.ILatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object ILatLngSerializer : KSerializer<ILatLng> {

  override val descriptor: SerialDescriptor
    get() = buildClassSerialDescriptor("ILatLng") {
      element<Double>("lat")
      element<Double>("lng")
    }


  override fun serialize(encoder: Encoder, value: ILatLng) {
    encoder.encodeStructure(descriptor) {
      encodeDoubleElement(descriptor, 0, value.lat)
      encodeDoubleElement(descriptor, 1, value.lng)
    }
  }

  override fun deserialize(decoder: Decoder): ILatLng {
    var lat = 0.0
    var lng = 0.0
    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0 -> {
            val value = decodeDoubleElement(descriptor, 0)
            if (value < -90.0 || value > 90.0) {
              throw SerializationException("Invalid latitude value: $value. Latitude must be between -90 and 90.")
            }
            lat = value
          }

          1 -> {
            val value = decodeDoubleElement(descriptor, 1)
            if (value < -180.0 || value > 180.0) {
              throw SerializationException("Invalid longitude value: $value. Longitude must be between -180 and 180.")
            }
            lng = value
          }
          else -> break@loop
        }
      }
    }
    return object : ILatLng {
      override val lat: Double = lat
      override val lng: Double = lng
    }
  }


}
