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


object IBirthDataSerializer : KSerializer<IBirthData> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IBirthData") {
    element<ITimeLoc>("timeLoc")
    element<Gender>("gender")
  }


  override fun serialize(encoder: Encoder, value: IBirthData) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ITimeLocSerializer, value)
      encodeSerializableElement(descriptor, 1, GenderSerializer, value.gender)
    }
  }

  override fun deserialize(decoder: Decoder): IBirthData {
    var timeLoc: ITimeLoc = TimeLoc(LocalDateTime.now(), locationOf(Locale.TAIWAN))
    var gender = Gender.男

    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> timeLoc = decodeSerializableElement(descriptor, 0, ITimeLocSerializer)
          1    -> gender = decodeSerializableElement(descriptor, 1, GenderSerializer)
          else -> break@loop
        }
      }
    }
    return BirthData(timeLoc, gender)
  }
}
