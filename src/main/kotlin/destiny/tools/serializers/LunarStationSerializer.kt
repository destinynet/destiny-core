/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.tools.serializers

import destiny.core.astrology.LunarStation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object LunarStationSerializer : KSerializer<LunarStation> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LunarStation" , PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LunarStation) {
    encoder.encodeString(value.nameKey)
  }

  override fun deserialize(decoder: Decoder): LunarStation {
    val raw = decoder.decodeString()
    return LunarStation.list.first { it.nameKey == raw }
  }
}
