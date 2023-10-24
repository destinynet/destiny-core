/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.ITimeLoc
import destiny.core.TimeLoc
import destiny.core.calendar.ILocation
import destiny.core.calendar.locationOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


object ITimeLocSerializer : KSerializer<ITimeLoc> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ITimeLoc") {
    element<Long>("time")
    element<ILocation>("loc")
  }

  override fun serialize(encoder: Encoder, value: ITimeLoc) {
    val zoneId = ZoneId.systemDefault()
    val epochSec = (value.time as LocalDateTime).atZone(zoneId).toEpochSecond()
    encoder.encodeStructure(descriptor) {
      // TODO : encodeSerializableElement(descriptor, 0, LocalDateTimeSerializer, value.time as LocalDateTime)
      encodeLongElement(descriptor, 0, epochSec)
      encodeSerializableElement(descriptor, 1, ILocationSerializer, value.location)
    }
  }

  override fun deserialize(decoder: Decoder): ITimeLoc {
    val zoneId = ZoneId.systemDefault()
    var time = LocalDateTime.now()
    var loc: ILocation = locationOf(Locale.TAIWAN)
    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> time = run {
            // TODO : check decodeSerializableElement(descriptor, 0, LocalDateTimeSerializer)
            val epochSec = decodeLongElement(descriptor, 0)
            LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSec), zoneId)
          }

          1    -> loc = decodeSerializableElement(descriptor, 1, ILocationSerializer)
          else -> break@loop
        }
      }
    }
    return TimeLoc(time, loc)
  }
}
