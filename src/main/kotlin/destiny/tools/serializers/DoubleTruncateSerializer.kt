/**
 * Created by smallufo on 2025-06-23.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import destiny.tools.truncate


/**
 * 無條件捨去到 [decimals] 位小數。**重複編碼結果不變**：解碼後再編碼（結果凍結成 JSON 再輸出）數字不會變小。
 * 捨位的算法只有一份：[destiny.tools.truncate]
 */
open class DoubleTruncateSerializer(private val decimals: Int) : KSerializer<Double> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Double", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Double) {
    encoder.encodeDouble(value.truncate(decimals))
  }

  override fun deserialize(decoder: Decoder): Double {
    return decoder.decodeDouble()
  }
}
