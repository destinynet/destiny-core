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
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.ulp


/**
 * 無條件捨去到 [decimals] 位小數。**重複編碼結果不變**：解碼後再編碼（結果凍結成 JSON 再輸出）數字不會變小。
 *
 * 直接 `floor(value * factor)` 做不到：16.58 × 100 在浮點數裡是 1657.9999999999998，會捨成 16.57。
 * 所以乘積離整數只差浮點誤差（幾個 ulp）時，當作那個整數。
 */
open class DoubleTruncateSerializer(private val decimals: Int) : KSerializer<Double> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Double", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Double) {
    val factor = 10.0.pow(decimals)
    val scaled = value * factor
    val nearest = round(scaled)
    val whole = if (abs(scaled - nearest) <= scaled.ulp * ULPS) nearest else floor(scaled)
    encoder.encodeDouble(whole / factor)
  }

  override fun deserialize(decoder: Decoder): Double {
    return decoder.decodeDouble()
  }

  private companion object {
    /** 乘上 10 的次方最多差約 1 ulp；留一點餘裕，但遠小於任何真正的小數差距 */
    const val ULPS = 4
  }
}
