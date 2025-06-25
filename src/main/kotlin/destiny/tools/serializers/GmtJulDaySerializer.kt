/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.tools.serializers

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.toLmt
import destiny.tools.roundToNearestSecond
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneId


object GmtJulDaySerializer : KSerializer<GmtJulDay> {

  val julDayResolver = JulDayResolver1582CutoverImpl()

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IGmtJulDay") {
    element<Double>("julDay")
    element<String>("gmt")
  }


  override fun serialize(encoder: Encoder, value: GmtJulDay) {
    val composite = encoder.beginStructure(descriptor)

    val jd = value.value  // 取 raw double 值
    composite.encodeDoubleElement(descriptor, 0, jd)

    val gmtStr = value.toLmt(ZoneId.of("UTC"), julDayResolver)
      .let { it as LocalDateTime }
      .roundToNearestSecond()
      .toString()  // 轉成 ISO 格式

    composite.encodeStringElement(descriptor, 1, gmtStr)

    composite.endStructure(descriptor)
  }


  override fun deserialize(decoder: Decoder): GmtJulDay {

    val dec = decoder.beginStructure(descriptor)

    var jd: Double? = null

    loop@ while (true) {
      when (val index = dec.decodeElementIndex(descriptor)) {
        0                            -> jd = dec.decodeDoubleElement(descriptor, 0)
        1                            -> dec.decodeStringElement(descriptor, 1) // 忽略 gmt
        CompositeDecoder.DECODE_DONE -> break@loop
        else                         -> throw SerializationException("Unexpected index: $index")
      }
    }

    dec.endStructure(descriptor)

    return jd?.toGmtJulDay() ?: throw SerializationException("Missing julDay field")
  }

}
