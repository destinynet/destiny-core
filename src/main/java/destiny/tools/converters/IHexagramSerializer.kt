/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.tools.converters

import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object IHexagramSerializer : KSerializer<IHexagram> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hexagram", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: IHexagram) {
    encoder.encodeString(
      value.yinYangs.joinToString("") { yy -> if (yy.booleanValue) "1" else "0" }
    )
  }

  override fun deserialize(decoder: Decoder): IHexagram {
    return decoder.decodeString().map { c ->
      when (c) {
        '1'  -> true
        '0'  -> false
        else -> throw IllegalArgumentException("Cannot decode '$c'")
      }
    }.let { booleans ->
      Hexagram.of(booleans)
    }
  }
}
