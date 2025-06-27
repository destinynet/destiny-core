/**
 * Created by smallufo on 2025-06-28.
 */
package destiny.tools.serializers

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toLmt
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.ZoneId


class GmtJulDayLocalDateSerializer(private val zoneId: ZoneId, private val julDayResolver: JulDayResolver) : KSerializer<GmtJulDay> {
  override val descriptor: SerialDescriptor = LocalDateSerializer.descriptor
  override fun serialize(encoder: Encoder, value: GmtJulDay) {
    val localDate = value.toLmt(zoneId, julDayResolver).toLocalDate() as LocalDate
    encoder.encodeSerializableValue(LocalDateSerializer, localDate)
  }

  override fun deserialize(decoder: Decoder): GmtJulDay {
    val localDate = decoder.decodeSerializableValue(LocalDateSerializer)
    return localDate.atStartOfDay().toGmtJulDay(zoneId)
  }
}
