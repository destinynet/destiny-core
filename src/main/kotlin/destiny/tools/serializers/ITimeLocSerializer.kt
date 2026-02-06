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
    element<Long>("epochSecond")
    element<ILocation>("loc")
    element<String>("localDateTime", isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: ITimeLoc) {
    val ldt = value.time as LocalDateTime
    val zoneId = value.location.tzid?.let { ZoneId.of(it) } ?: ZoneId.systemDefault()
    val epochSec = ldt.atZone(zoneId).toEpochSecond()
    encoder.encodeStructure(descriptor) {
      encodeLongElement(descriptor, 0, epochSec)
      encodeSerializableElement(descriptor, 1, ILocationSerializer, value.location)
      encodeStringElement(descriptor, 2, ldt.toString()) // 輔助資訊
    }
  }

  override fun deserialize(decoder: Decoder): ITimeLoc {
    var epochSec: Long? = null
    var localDateTimeStr: String? = null
    var loc: ILocation = locationOf(Locale.TAIWAN)
    decoder.decodeStructure(descriptor) {
      loop@ while (true) {
        when (decodeElementIndex(descriptor)) {
          0    -> epochSec = decodeLongElement(descriptor, 0)
          1    -> loc = decodeSerializableElement(descriptor, 1, ILocationSerializer)
          2    -> localDateTimeStr = decodeStringElement(descriptor, 2)
          else -> break@loop
        }
      }
    }
    // 優先使用 localDateTime（source of truth），fallback 到 epochSecond + location timezone
    val time = if (localDateTimeStr != null) {
      LocalDateTime.parse(localDateTimeStr)
    } else {
      val zoneId = loc.tzid?.let { ZoneId.of(it) } ?: ZoneId.systemDefault()
      LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSec!!), zoneId)
    }
    return TimeLoc(time, loc)
  }
}
