/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
  private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalDateTime) {
    encoder.encodeString(dateTimeFormatter.format(value))
  }

  override fun deserialize(decoder: Decoder): LocalDateTime {
    val stringValue = decoder.decodeString()
    return LocalDateTime.parse(stringValue, dateTimeFormatter)
  }
}
