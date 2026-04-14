/**
 * Created by smallufo on 2026-04-14.
 */
package destiny.tools.serializers

import destiny.core.iching.HexagramDefaultComparator
import destiny.core.iching.IHexagram
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IHexagramDefaultSeqSerializer : KSerializer<IHexagram> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hexagram", PrimitiveKind.INT)

  override fun serialize(encoder: Encoder, value: IHexagram) {
    encoder.encodeInt(HexagramDefaultComparator.instance.getIndex(value))
  }

  override fun deserialize(decoder: Decoder): IHexagram {
    return decoder.decodeInt().let { index ->
      HexagramDefaultComparator.instance.getHexagram(index)
    }
  }
}
