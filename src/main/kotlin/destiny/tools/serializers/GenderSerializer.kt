/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers

import destiny.core.Gender
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * [Gender] 的 kotlinx serializer —— 輸出 `M` / `F` , 但解碼時 `M`/`F` 與 `男`/`女` 都吃。
 *
 * **凡是會被 kotlinx 序列化的 [Gender] 欄位 , 一律標上 `@Serializable(with = GenderSerializer::class)`。**
 *
 * 2025-12-28 的 commit `2b6a2b3b`「Gender 男/女 -> M/F」把 enum 常數本身改名了。kotlinx 內建的
 * enum serializer 是以「常數名稱」比對的 , 所以裸的 `Gender` 欄位在改名後 , 就再也讀不懂改名前
 * 產生的 `"gender":"男"` , 而是拋出
 *
 *     SerializationException: destiny.core.Gender does not contain element with name '男'
 *
 * 這類舊 payload 的壽命遠比想像中長 —— LINE 對話紀錄裡的卦圖連結是永久的 , DB 的 JSONB 欄位也是。
 * (實例: 2026-08-22 `/chart/v1/squaredPairChart.png` 因為 `SquaredPairChartConfig` 漏標而整張圖 500 。)
 *
 * 標上它不會改變輸出格式(本來就是 M/F) , 純粹是把解碼放寬。
 */
object GenderSerializer : KSerializer<Gender> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Gender", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Gender) {
    val result = when (value) {
      Gender.M -> "M"
      Gender.F -> "F"
    }
    encoder.encodeString(result)
  }

  override fun deserialize(decoder: Decoder): Gender {
    return decoder.decodeString().let { raw ->
      when (raw.uppercase()) {
        "M", "男" -> Gender.M
        "F", "女" -> Gender.F
        else      -> throw IllegalArgumentException("Invalid gender value : $raw")
      }
    }
  }
}
