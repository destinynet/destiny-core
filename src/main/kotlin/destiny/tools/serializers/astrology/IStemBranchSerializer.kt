package destiny.tools.serializers.astrology

import destiny.core.chinese.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IStemBranchSerializer : KSerializer<IStemBranch> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("stemBranch", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: IStemBranch) {
    return encoder.encodeString(value.stem.toString()+value.branch.toString())
  }

  override fun deserialize(decoder: Decoder): IStemBranch {

    return decoder.decodeString().takeIf { it.length == 2 }?.let { it[0] to it[1] }
      ?.let { (s, b) -> Stem[s] to Branch[b] }
      ?.takeIf { (s, b) -> s != null && b != null }
      ?.let { (s, b) -> s!! to b!! }
      ?.let { (s, b) ->
        if (s.index % 2 == b.index % 2) {
          StemBranch[s, b]
        } else {
          StemBranchUnconstrained[s, b]
        }
      } ?: throw IllegalArgumentException("invalid stemBranch string : ${decoder.decodeString()}")
  }
}
