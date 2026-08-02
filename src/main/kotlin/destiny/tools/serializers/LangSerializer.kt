/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools.serializers

import destiny.tools.Lang
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Lang] 的序列化器 —— 產出的 JSON 與 [LocaleSerializer] **逐字相同**（BCP-47 tag 字串）。
 *
 * 這是 [Lang] 能逐步取代 `java.util.Locale` 的關鍵：兩者可長期並存，
 * DTO 一個一個換，既有的持久化資料與 API 回應格式都不受影響。
 * `LangSerializerTest` 對使用中的語言逐一比對兩者輸出。
 *
 * [Lang.ROOT] 編碼為 `"und"`，與 `Locale.ROOT.toLanguageTag()` 一致。
 */
object LangSerializer : KSerializer<Lang> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("lang", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Lang) {
    encoder.encodeString(if (value.isRoot) UNDETERMINED else value.tag)
  }

  /**
   * 無法解析者回傳 [Lang.ROOT]，與 `Locale.forLanguageTag()` 對垃圾輸入的行為一致。
   *
   * `replace("\"", "")` 沿用 [LocaleSerializer] 的防禦性處理（曾遇過帶引號的值）。
   */
  override fun deserialize(decoder: Decoder): Lang {
    val str = decoder.decodeString().replace("\"", "")
    return Lang.of(str) ?: Lang.ROOT
  }

  private const val UNDETERMINED = "und"
}
