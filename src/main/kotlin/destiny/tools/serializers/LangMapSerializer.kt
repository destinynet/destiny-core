/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools.serializers

import destiny.tools.Lang
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * `Map<Lang, String>` 的序列化器 —— i18n 欄位用。
 *
 * 產出的 JSON 與 [LocaleMapSerializer] **逐字相同**：
 * ```
 * mapOf(Lang.EN to "Vocation", Lang.ZH_TW to "職業")
 *   ↔  {"en":"Vocation","zh-TW":"職業"}
 * ```
 *
 * 格式一致很重要 —— [LocaleMapSerializer] 被 destiny-jakarta-ee 的
 * `LocaleMapConverter`（JPA `AttributeConverter`）用來讀寫 DB 的 JSONB 欄位，
 * 兩者必須能互相解讀既有資料。`LangSerializerTest` 逐一比對。
 */
object LangMapSerializer : KSerializer<Map<Lang, String>> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LangMap")

  override fun serialize(encoder: Encoder, value: Map<Lang, String>) {
    val jsonEncoder = encoder as JsonEncoder
    jsonEncoder.encodeJsonElement(
      buildJsonObject {
        value.forEach { (lang, name) ->
          put(if (lang.isRoot) "und" else lang.tag, JsonPrimitive(name))
        }
      }
    )
  }

  override fun deserialize(decoder: Decoder): Map<Lang, String> {
    val jsonDecoder = decoder as JsonDecoder
    return jsonDecoder.decodeJsonElement().jsonObject
      .mapKeys { (key, _) -> Lang.of(key) ?: Lang.ROOT }
      .mapValues { (_, value) -> value.jsonPrimitive.content }
  }
}
