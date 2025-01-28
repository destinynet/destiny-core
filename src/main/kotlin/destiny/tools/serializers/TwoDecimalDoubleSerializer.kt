package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DecimalFormat

object TwoDecimalDoubleSerializer : KSerializer<Double> {
  private val decimalFormat = DecimalFormat("#.##")

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Double", PrimitiveKind.DOUBLE)

  override fun serialize(encoder: Encoder, value: Double) {
    val formattedValue = decimalFormat.format(value).toDouble()
    encoder.encodeDouble(formattedValue)
  }

  override fun deserialize(decoder: Decoder): Double {
    return decoder.decodeDouble()
  }
}
