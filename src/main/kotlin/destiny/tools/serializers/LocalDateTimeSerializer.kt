/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
  private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME // ISO_DATE_TIME 格式為 YYYY-MM-DDTHH:MM:SS.NNNNNNNNN

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalDateTime) {
    encoder.encodeString(dateTimeFormatter.format(value))
  }

  override fun deserialize(decoder: Decoder): LocalDateTime {
    // 檢查 decoder 是否為 JsonDecoder，以便訪問底層的 JsonElement
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    val element = jsonDecoder.decodeJsonElement()

    return when {
      // 如果是 JSON 字串 (e.g., "2024-07-22T15:30:45")
      element is JsonPrimitive && element.isString -> {
        try {
          LocalDateTime.parse(element.content, dateTimeFormatter)
        } catch (e: DateTimeParseException) {
          throw IllegalStateException("Invalid LocalDateTime string format: ${element.content}", e)
        }
      }
      // 如果是 JSON 物件 (e.g., { "year": 2025, "month": 7, "day": 22, "hour": 15, "minute": 30, "second": 45, "nano": 123456789 })
      element is JsonObject -> {
        val year = element["year"]?.jsonPrimitive?.int
        val month = element["month"]?.jsonPrimitive?.int
        val day = element["day"]?.jsonPrimitive?.int
        val hour = element["hour"]?.jsonPrimitive?.int
        val minute = element["minute"]?.jsonPrimitive?.int
        val second = element["second"]?.jsonPrimitive?.int ?: 0 // second 和 nano 可以是可選的
        val nano = element["nano"]?.jsonPrimitive?.int ?: 0

        if (year != null && month != null && day != null && hour != null && minute != null) {
          try {
            LocalDateTime.of(year, month, day, hour, minute, second, nano)
          } catch (e: Exception) {
            throw IllegalStateException("Invalid LocalDateTime object format: $element", e)
          }
        } else {
          throw IllegalStateException("LocalDateTime object must contain 'year', 'month', 'day', 'hour', and 'minute' fields: $element")
        }
      }

      else                                         -> {
        throw IllegalStateException("Unsupported LocalDateTime format. Expected string (ISO_DATE_TIME) or object ({year, month, day, hour, minute, second, nano}). Received: $element")
      }
    }
  }
}
