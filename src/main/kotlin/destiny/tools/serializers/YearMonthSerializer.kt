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
import java.time.DateTimeException
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

    val element: JsonElement = jsonDecoder.decodeJsonElement()

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
        extractYearMonthFields(element)
      }

      else                                         -> {
        throw IllegalStateException("Unsupported YearMonth format. Expected string (YYYY-MM) or object ({year, month}). Received: $element")
      }
    }
  }

  /**
   * 從 JsonObject 中提取 'year' 和 'month' 欄位並構建 YearMonth 物件。
   * 這個方法可以被其他序列化器重用。
   */
  fun extractYearMonthFields(jsonObject: JsonObject): YearMonth {
    val year = jsonObject["year"]?.jsonPrimitive?.int
    val month = jsonObject["month"]?.jsonPrimitive?.int

    if (year != null && month != null) {
      try {
        return YearMonth.of(year, month)
      } catch (e: DateTimeException) {
        throw IllegalStateException("Invalid YearMonth object format: $jsonObject", e)
      }
    } else {
      throw IllegalStateException("YearMonth object must contain 'year' and 'month' fields: $jsonObject")
    }
  }
}
