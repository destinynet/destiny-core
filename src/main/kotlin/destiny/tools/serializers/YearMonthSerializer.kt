/**
 * Created by smallufo on 2025-07-01.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.YearMonth
import java.time.format.DateTimeFormatter


object YearMonthSerializer : KSerializer<YearMonth> {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("YearMonth", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: YearMonth) {
    val string = value.format(formatter)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): YearMonth {
    val string = decoder.decodeString()
    return YearMonth.parse(string, formatter)
  }
}
