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
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


/** 只顯示到「分」*/
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
      .let {
        if (it is LocalDateTime)
          it.roundToNearestSecond().truncatedTo(ChronoUnit.MINUTES)
        else
          it
      }
      .toString()  // 轉成 ISO 格式

    composite.encodeStringElement(descriptor, 1, gmtStr)

    composite.endStructure(descriptor)
  }


  override fun deserialize(decoder: Decoder): GmtJulDay {

    return when (val jsonElement = (decoder as JsonDecoder).decodeJsonElement()) {
      // 處理前端只傳遞 Julian Day 基本型別的情況
      is kotlinx.serialization.json.JsonPrimitive -> {
        jsonElement.double.toGmtJulDay()
      }
      // 處理包含 julDay 和 gmt 的物件型別
      is kotlinx.serialization.json.JsonObject    -> {
        val jd = jsonElement.jsonObject["julDay"]?.jsonPrimitive?.double
        jd?.toGmtJulDay() ?: throw SerializationException("Missing julDay field")
      }
      // 拋出例外
      else                                        -> throw SerializationException("Unexpected JSON format for GmtJulDay")
    }
  }

}
