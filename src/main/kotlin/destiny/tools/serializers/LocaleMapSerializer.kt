/**
 * Created by smallufo on 2025-12-28.
 *
 * Serializer for Map<Locale, String> used in i18n fields.
 * Reuses LocaleSerializer for Locale key serialization.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.*

/**
 * Serializer for Map<Locale, String> that converts to/from JSON format.
 *
 * Example:
 * - Kotlin: mapOf(Locale.ENGLISH to "Vocation", Locale.forLanguageTag("zh_TW") to "職業")
 * - JSON: {"en": "Vocation", "zh_TW": "職業"}
 */
object LocaleMapSerializer : KSerializer<Map<Locale, String>> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LocaleMap")

  override fun serialize(encoder: Encoder, value: Map<Locale, String>) {
    val jsonEncoder = encoder as JsonEncoder
    val jsonObject = buildJsonObject {
      value.forEach { (locale, name) ->
        put(locale.toLanguageTag(), JsonPrimitive(name))
      }
    }
    jsonEncoder.encodeJsonElement(jsonObject)
  }

  override fun deserialize(decoder: Decoder): Map<Locale, String> {
    val jsonDecoder = decoder as JsonDecoder
    val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

    return jsonObject.mapKeys { (key, _) ->
      Locale.forLanguageTag(key)
    }.mapValues { (_, value) ->
      value.jsonPrimitive.content
    }
  }
}
