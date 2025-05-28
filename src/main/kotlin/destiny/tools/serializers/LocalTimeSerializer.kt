/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter


object LocalTimeSerializer : KSerializer<LocalTime> {
  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalTime) {
    val string = value.format(formatter)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): LocalTime {
    val string = decoder.decodeString()
    return LocalTime.parse(string, formatter)
  }

}
