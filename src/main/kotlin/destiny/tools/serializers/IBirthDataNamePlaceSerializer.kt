/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.*
import destiny.core.calendar.locationOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.LocalDateTime
import java.util.*


object IBirthDataNamePlaceSerializer : KSerializer<IBirthDataNamePlace> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IBirthDataNamePlace") {
    element<IBirthData>("birthData")
    element<String>("name", isOptional = true)
    element<String>("place", isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: IBirthDataNamePlace) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, IBirthDataSerializer, value)
      value.name?.also { encodeStringElement(descriptor, 1, it) }
      value.place?.also { encodeStringElement(descriptor, 2, it) }
    }
  }

  override fun deserialize(decoder: Decoder): IBirthDataNamePlace {
    var birthData: IBirthData = BirthData(LocalDateTime.now(), locationOf(Locale.TAIWAN), Gender.男)
    var name: String? = null
    var place: String? = null


    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> birthData = decodeSerializableElement(descriptor, 0, IBirthDataSerializer)
          1    -> name = decodeStringElement(descriptor, 1)
          2    -> place = decodeStringElement(descriptor, 2)
          else -> break@loop
        }
      }
    }
    return BirthDataNamePlace(birthData, name, place)
  }
}
