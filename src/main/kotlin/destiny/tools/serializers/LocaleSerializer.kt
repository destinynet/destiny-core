package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import destiny.tools.Lang
import destiny.tools.toLocale
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * 寫入一律 BCP-47（`Locale.TAIWAN` → `"zh-TW"`）；
 * 讀取則**額外**容忍 legacy 的底線形式（`"zh_TW"`、`"zh_HANT"`）。
 *
 * 讀取端不可直接用 `Locale.forLanguageTag()` —— 它只吃連字號，遇到底線
 * 不丟例外而是靜默回傳 `Locale.ROOT`。改走 [Lang.of]，兩種分隔符號都吃，
 * 且能正確辨識 script 子標籤。
 */
object LocaleSerializer : KSerializer<Locale> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("locale", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Locale) {

    encoder.encodeString(value.toLanguageTag())
  }

  override fun deserialize(decoder: Decoder): Locale {
    val str = decoder.decodeString().replace("\"","")
    return Lang.of(str)?.toLocale() ?: Locale.ROOT
  }

}
