/**
 * Created by smallufo on 2025-07-13.
 */
package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI

object UriSerializer : KSerializer<URI> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.net.URI", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: URI) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): URI {
    return URI(decoder.decodeString())
  }
}
