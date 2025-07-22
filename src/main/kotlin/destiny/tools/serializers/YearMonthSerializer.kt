/**
 * Created by smallufo on 2025-07-01.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object YearMonthSerializer : KSerializer<YearMonth> {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("YearMonth", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: YearMonth) {
    val string = value.format(formatter)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): YearMonth {
    // 檢查 decoder 是否為 JsonDecoder，以便訪問底層的 JsonElement
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    val element = jsonDecoder.decodeJsonElement()

    return when {
      // 如果是 JSON 字串 (e.g., "2024-12")
      element is JsonPrimitive && element.isString -> {
        try {
          YearMonth.parse(element.content, formatter)
        } catch (e: DateTimeParseException) {
          throw IllegalStateException("Invalid YearMonth string format: ${element.content}", e)
        }
      }
      // 如果是 JSON 物件 (e.g., { "year": 2024, "month": 12 })
      element is JsonObject -> {
        val year = element["year"]?.jsonPrimitive?.int
        val month = element["month"]?.jsonPrimitive?.int

        if (year != null && month != null) {
          try {
            YearMonth.of(year, month)
          } catch (e: Exception) {
            throw IllegalStateException("Invalid YearMonth object format: $element", e)
          }
        } else {
          throw IllegalStateException("YearMonth object must contain 'year' and 'month' fields: $element")
        }
      }

      else                                         -> {
        throw IllegalStateException("Unsupported YearMonth format. Expected string (YYYY-MM) or object ({year, month}). Received: $element")
      }
    }
  }
}
