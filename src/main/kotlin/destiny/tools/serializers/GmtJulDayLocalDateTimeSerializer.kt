/**
 * Created by smallufo on 2025-06-27.
 */
package destiny.tools.serializers

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toLmt
import destiny.tools.roundToNearestSecond
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


class GmtJulDayLocalDateTimeSerializer(private val zoneId: ZoneId, private val julDayResolver: JulDayResolver) : KSerializer<GmtJulDay> {
  override val descriptor: SerialDescriptor = LocalDateTimeSerializer.descriptor
  override fun serialize(encoder: Encoder, value: GmtJulDay) {
    val localDateTime = (value.toLmt(zoneId, julDayResolver) as LocalDateTime).roundToNearestSecond().truncatedTo(ChronoUnit.MINUTES)
    encoder.encodeSerializableValue(LocalDateTimeSerializer, localDateTime)
  }

  override fun deserialize(decoder: Decoder): GmtJulDay {
    val localDateTime = decoder.decodeSerializableValue(LocalDateTimeSerializer)
    return localDateTime.toGmtJulDay(zoneId)
  }
}
