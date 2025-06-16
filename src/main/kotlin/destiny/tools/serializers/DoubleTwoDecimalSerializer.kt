/**
 * Created by smallufo on 2025-06-17.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.round


object DoubleTwoDecimalSerializer : KSerializer<Double> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Double", PrimitiveKind.DOUBLE)


  override fun serialize(encoder: Encoder, value: Double) {
    // 先將數值乘上 100，四捨五入後，再除以 100.0 來得到小數點後兩位
    val roundedValue = round(value * 100) / 100.0
    encoder.encodeDouble(roundedValue)
  }

  override fun deserialize(decoder: Decoder): Double {
    // 反序列化時，直接讀取即可
    return decoder.decodeDouble()
  }
}
