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
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
  // 序列化時使用標準格式
  private val serializeFormatter = DateTimeFormatter.ISO_DATE_TIME

  // 反序列化時使用更有彈性的格式，允許秒和毫秒是可選的
  private val deserializeFormatter = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .optionalStart()
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .optionalStart()
    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
    .optionalEnd()
    .optionalEnd()
    .toFormatter()

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalDateTime) {
    encoder.encodeString(serializeFormatter.format(value))
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
          LocalDateTime.parse(element.content, deserializeFormatter)
        } catch (e: DateTimeParseException) {
          throw IllegalStateException("Invalid LocalDateTime string format: ${element.content}", e)
        }
      }
      // 如果是 JSON 物件 (e.g., { "year": 2025, "month": 7, "day": 22, "hour": 15, "minute": 30, "second": 45, "nano": 123456789 })
      element is JsonObject                        -> {
        val localDate = LocalDateSerializer.extractLocalDateFields(element)
        val localTime = LocalTimeSerializer.extractLocalTimeFields(element)

        try {
          return LocalDateTime.of(localDate, localTime)
        } catch (e: DateTimeException) {
          throw IllegalStateException("Invalid LocalDateTime object format: $element", e)
        }
      }

      else                                         -> {
        throw IllegalStateException("Unsupported LocalDateTime format. Expected string (ISO_DATE_TIME) or object ({year, month, day, hour, minute, second, nano}). Received: $element")
      }
    }
  }
}
