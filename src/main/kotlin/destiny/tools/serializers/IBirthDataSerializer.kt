/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.*
import destiny.core.calendar.locationOf
import destiny.tools.KotlinLogging
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.time.LocalDateTime
import java.util.*


object IBirthDataSerializer : KSerializer<IBirthData> {

  private val logger = KotlinLogging.logger { }

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
    var gender = Gender.M

    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> timeLoc = decodeSerializableElement(descriptor, 0, ITimeLocSerializer)
          1                            -> gender = try {
            decodeSerializableElement(descriptor, 1, GenderSerializer)
          } catch (e: Exception) {
            logger.warn { "GENDER_NOT_FOUND , default to MALE" }
            Gender.M
          }

          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return BirthData(timeLoc, gender)
  }
}
