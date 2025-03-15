/**
 * Created by smallufo on 2025-03-15.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object LocalDateSerializer : KSerializer<LocalDate> {
  private val dateFormatter = DateTimeFormatter.ISO_DATE

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalDate) {
    encoder.encodeString(dateFormatter.format(value))
  }

  override fun deserialize(decoder: Decoder): LocalDate {
    val stringValue = decoder.decodeString()
    return LocalDate.parse(stringValue, dateFormatter)
  }
}

