/**
 * Created by smallufo on 2025-05-14.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


object AnyValueSerializer : KSerializer<Any> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AnyValue", PrimitiveKind.STRING) // 描述符可以簡化，因為它很動態

  override fun serialize(encoder: Encoder, value: Any) {
    // 這裡主要關心反序列化，序列化可以根據需要實現或拋出不支持異常
    val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("This serializer can be used only with Json")
    when (value) {
      is String  -> jsonEncoder.encodeString(value)
      is Int     -> jsonEncoder.encodeInt(value)
      is Long    -> jsonEncoder.encodeLong(value)
      is Double  -> jsonEncoder.encodeDouble(value)
      is Boolean -> jsonEncoder.encodeBoolean(value)
      // 可以根據需要添加對其他類型的序列化支持
      else       -> throw SerializationException("Unsupported type: ${value::class}")
    }
  }

  override fun deserialize(decoder: Decoder): Any {
    val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("This serializer can be used only with Json")
    val jsonElement = jsonDecoder.decodeJsonElement()

    return when (jsonElement) {
      is JsonNull      -> throw SerializationException("Null value not supported for Any by this serializer") // 或者返回一個特定的 null 標記對象
      is JsonPrimitive -> {
        if (jsonElement.isString) {
          jsonElement.content
        } else if (jsonElement.booleanOrNull != null) {
          jsonElement.boolean
        } else if (jsonElement.longOrNull != null) { // 先嘗試 Long
          jsonElement.long
        } else if (jsonElement.doubleOrNull != null) { // 再嘗試 Double
          jsonElement.double
        } else {
          // 預設回退到字符串內容，或者拋出異常
          jsonElement.content
        }
      }
      // 不會有 array/list，所以這裡可以簡化
      is JsonObject    -> throw SerializationException("JsonObject not supported for Any by this serializer, expected primitive.")
      is JsonArray     -> throw SerializationException("JsonArray not supported for Any by this serializer, expected primitive.")
    }
  }
}
