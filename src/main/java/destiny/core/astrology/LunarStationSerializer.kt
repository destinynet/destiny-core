/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.astrology

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class LunarStationSerializer : KSerializer<LunarStation> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LunarStation" , PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LunarStation) {
    encoder.encodeString(value.nameKey)
  }

  override fun deserialize(decoder: Decoder): LunarStation {
    val raw = decoder.decodeString()
    return LunarStation.values.first { it.nameKey == raw }
  }
}
