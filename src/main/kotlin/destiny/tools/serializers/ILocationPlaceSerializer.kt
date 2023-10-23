/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import destiny.core.calendar.LocationPlace
import destiny.core.calendar.locationOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.util.*


object ILocationPlaceSerializer : KSerializer<ILocationPlace> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ILocationPlace") {
    element<ILocation>("loc")
    element<String>("place")
  }

  override fun serialize(encoder: Encoder, value: ILocationPlace) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ILocationSerializer, value)
      encodeStringElement(descriptor, 1, value.place)
    }
  }

  override fun deserialize(decoder: Decoder): ILocationPlace {
    var loc: ILocation = locationOf(Locale.TAIWAN)
    var place = ""
    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> loc = decodeSerializableElement(descriptor, 0, ILocationSerializer)
          1    -> place = decodeStringElement(descriptor, 1)
          else -> break@loop
        }
      }
    }
    return LocationPlace(loc, place)
  }
}
