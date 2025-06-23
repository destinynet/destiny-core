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
import kotlin.math.floor
import kotlin.math.pow


open class DoubleTruncateSerializer(private val decimals: Int) : KSerializer<Double> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Double", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Double) {
    val factor = 10.0.pow(decimals)
    val truncated = floor(value * factor) / factor
    encoder.encodeDouble(truncated)
  }

  override fun deserialize(decoder: Decoder): Double {
    return decoder.decodeDouble()
  }
}
