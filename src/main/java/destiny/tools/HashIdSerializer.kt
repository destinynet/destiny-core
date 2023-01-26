/**
 * Created by smallufo on 2023-01-26.
 */
package destiny.tools

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class HashIdSerializer(key : String) : KSerializer<Long> {

  private val hashids = Hashids(key)

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hashId", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Long {
    return hashids.decode(decoder.decodeString())[0]
  }

  override fun serialize(encoder: Encoder, value: Long) {
    encoder.encodeString(hashids.encode(value))
  }

}
