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
import kotlinx.serialization.json.*
import java.time.DateTimeException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object LocalTimeSerializer : KSerializer<LocalTime> {
  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME // ISO_LOCAL_TIME 格式為 HH:MM:SS.NNNNNNNNN

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalTime) {
    val string = value.format(formatter)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): LocalTime {
    // 檢查 decoder 是否為 JsonDecoder，以便訪問底層的 JsonElement
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    val element = jsonDecoder.decodeJsonElement()

    return when {
      // 如果是 JSON 字串 (e.g., "15:30:45")
      element is JsonPrimitive && element.isString -> {
        try {
          LocalTime.parse(element.content, formatter)
        } catch (e: DateTimeParseException) {
          throw IllegalStateException("Invalid LocalTime string format: ${element.content}", e)
        }
      }
      // 如果是 JSON 物件 (e.g., { "hour": 15, "minute": 30, "second": 45, "nano": 123456789 })
      element is JsonObject -> {
        extractLocalTimeFields(element)
      }

      else                                         -> {
        throw IllegalStateException("Unsupported LocalTime format. Expected string (ISO_LOCAL_TIME) or object ({hour, minute, second, nano}). Received: $element")
      }
    }
  }

  fun extractLocalTimeFields(jsonObject: JsonObject): LocalTime {
    val hour = jsonObject["hour"]?.jsonPrimitive?.int
    val minute = jsonObject["minute"]?.jsonPrimitive?.int
    val second = jsonObject["second"]?.jsonPrimitive?.int ?: 0 // second 和 nano 可以是可選的
    val nano = jsonObject["nano"]?.jsonPrimitive?.int ?: 0

    if (hour != null && minute != null) {
      try {
        return LocalTime.of(hour, minute, second, nano)
      } catch (e: DateTimeException) {
        throw IllegalStateException("Invalid LocalTime object format: $jsonObject", e)
      }
    } else {
      throw IllegalStateException("LocalTime object must contain 'hour' and 'minute' fields: $jsonObject")
    }
  }
}
