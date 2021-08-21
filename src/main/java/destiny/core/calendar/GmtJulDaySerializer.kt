/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.calendar

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class GmtJulDaySerializer : KSerializer<GmtJulDay> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("gmtJulDay", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: GmtJulDay) {
    encoder.encodeDouble(value.value)
  }

  override fun deserialize(decoder: Decoder): GmtJulDay {
    val value = decoder.decodeDouble()
    return GmtJulDay(value)
  }
}
