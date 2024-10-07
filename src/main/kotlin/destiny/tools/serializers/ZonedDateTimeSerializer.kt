/**
 * Created by smallufo on 2023-10-29.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ZonedDateTime) {
    val formatted = value.format(DateTimeFormatter.ISO_INSTANT)
    encoder.encodeString(formatted)
  }

  override fun deserialize(decoder: Decoder): ZonedDateTime {
    val string = decoder.decodeString()
    return ZonedDateTime.parse(string, DateTimeFormatter.ISO_INSTANT)
  }
}
