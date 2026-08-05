/**
 * Created by smallufo on 2025-12-28.
 *
 * Serializer for Map<Locale, String> used in i18n fields.
 * Reuses LocaleSerializer for Locale key serialization.
 */
package destiny.tools.serializers

import destiny.tools.Lang
import destiny.tools.toLocale
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.util.*

/**
 * `Map<Locale, String>` 的序列化器 —— i18n 欄位用。[LangMapSerializer] 的 [Locale] 版。
 *
 * **寫入一律 BCP-47**（連字號）：
 * ```
 * mapOf(Locale.ENGLISH to "Vocation", Locale.TAIWAN to "職業")
 *   →  {"en":"Vocation","zh-TW":"職業"}
 * ```
 *
 * **讀取則額外容忍 legacy 的底線形式**（`{"zh_TW":"職業"}`）。這不是可有可無的寬容：
 * `Locale.forLanguageTag("zh_TW")` 不丟例外而是靜默回傳 `Locale.ROOT`，於是同一個 map 裡的
 * `zh_TW` 與 `zh_CN` 會**雙雙塌成 ROOT、後者覆蓋前者**，簡中譯名直接消失。
 * prod 的 `data.category.names` 865 筆正是這個形狀。改走 [Lang.of] 後兩種分隔符號都吃。
 *
 * （早期版本的本註解寫成 `Locale.forLanguageTag("zh_TW")` → `{"zh_TW": ...}` —— 那是做不到的：
 * 該呼叫回傳 ROOT，key 只會是 `"und"`。DB 裡的底線 key 來自別的寫入路徑。）
 *
 * 本序列化器被 destiny-jakarta-ee 的 `LocaleMapConverter`（JPA `AttributeConverter`）
 * 用來讀寫 JSONB 欄位，格式必須與 [LangMapSerializer] 互通。
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
      Lang.of(key)?.toLocale() ?: Locale.ROOT
    }.mapValues { (_, value) ->
      value.jsonPrimitive.content
    }
  }
}
