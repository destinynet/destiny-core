/**
 * Created by smallufo on 2025-12-18.
 *
 * Contextual serializer for Any type that supports complex nested structures.
 * Handles: Int, Long, Double, Float, String, Boolean, LocalDateTime, Map, List (including nested)
 *
 * Use this serializer when your Map<String, Any> contains complex nested structures.
 * For simple primitive values only, consider using [AnyValueSerializer] instead.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AnyComplexSerializer : KSerializer<Any> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AnyComplex")

  override fun serialize(encoder: Encoder, value: Any) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(serializeValue(value))
  }

  private fun serializeValue(value: Any): JsonElement = when (value) {
    is Int -> JsonPrimitive(value)
    is Long -> JsonPrimitive(value)
    is Double -> JsonPrimitive(value)
    is Float -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    is LocalDateTime -> JsonPrimitive(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    is Map<*, *> -> {
      buildJsonObject {
        value.forEach { (k, v) ->
          if (k is String && v != null) {
            put(k, serializeValue(v))
          }
        }
      }
    }
    is List<*> -> {
      buildJsonArray {
        value.forEach { item ->
          if (item != null) {
            add(serializeValue(item))
          }
        }
      }
    }
    else -> JsonPrimitive(value.toString())
  }

  override fun deserialize(decoder: Decoder): Any {
    val jsonDecoder = decoder as JsonDecoder
    val element = jsonDecoder.decodeJsonElement()
    return deserializeJsonElement(element)
  }

  private fun deserializeJsonElement(element: JsonElement): Any {
    return when (element) {
      is JsonPrimitive -> {
        when {
          element.isString -> element.content // Keep as String (including ISO dates)
          element.booleanOrNull != null -> element.boolean
          element.longOrNull != null -> element.long.let { if (it in Int.MIN_VALUE..Int.MAX_VALUE) it.toInt() else it }
          element.doubleOrNull != null -> element.double
          else -> element.content
        }
      }
      is JsonObject -> element.mapValues { (_, value) -> deserializeJsonElement(value) }
      is JsonArray -> element.map { deserializeJsonElement(it) }
    }
  }
}
