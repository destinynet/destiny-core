/**
 * Created by smallufo on 2025-03-15.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object LocalDateSerializer : KSerializer<LocalDate> {
  private val dateFormatter = DateTimeFormatter.ISO_DATE

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LocalDate) {
    encoder.encodeString(dateFormatter.format(value))
  }

  override fun deserialize(decoder: Decoder): LocalDate {
    // 檢查 decoder 是否為 JsonDecoder，以便訪問底層的 JsonElement
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    val element = jsonDecoder.decodeJsonElement()

    return when {
      // 如果是 JSON 字串 (e.g., "2025-07-22")
      element is JsonPrimitive && element.isString -> {
        try {
          LocalDate.parse(element.content, dateFormatter)
        } catch (e: DateTimeParseException) {
          throw IllegalStateException("Invalid LocalDate string format: ${element.content}", e)
        }
      }
      // 如果是 JSON 物件 (e.g., { "year": 2025, "month": 7, "day": 22 })
      element is JsonObject -> {
        val year = element["year"]?.jsonPrimitive?.int
        val month = element["month"]?.jsonPrimitive?.int
        val day = element["day"]?.jsonPrimitive?.int

        if (year != null && month != null && day != null) {
          try {
            LocalDate.of(year, month, day)
          } catch (e: Exception) {
            throw IllegalStateException("Invalid LocalDate object format: $element", e)
          }
        } else {
          throw IllegalStateException("LocalDate object must contain 'year', 'month', and 'day' fields: $element")
        }
      }

      else                                         -> {
        throw IllegalStateException("Unsupported LocalDate format. Expected string (YYYY-MM-DD) or object ({year, month, day}). Received: $element")
      }
    }
  }
}

