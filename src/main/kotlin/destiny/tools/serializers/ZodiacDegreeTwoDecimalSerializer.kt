package destiny.tools.serializers

import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.round

object ZodiacDegreeTwoDecimalSerializer : KSerializer<ZodiacDegree> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZodiacDegree", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: ZodiacDegree) {
    val doubleValue = value.value
    val roundedValue = round(doubleValue * 100) / 100.0
    encoder.encodeDouble(roundedValue)
  }

  override fun deserialize(decoder: Decoder): ZodiacDegree {
    return decoder.decodeDouble().toZodiacDegree()
  }
}
