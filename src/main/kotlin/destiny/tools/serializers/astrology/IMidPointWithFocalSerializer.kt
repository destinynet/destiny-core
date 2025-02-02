/**
 * Created by smallufo on 2025-01-26.
 */
package destiny.tools.serializers.astrology

import destiny.core.astrology.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*


object IMidPointWithFocalSerializer : KSerializer<IMidPointWithFocal> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IMidPointWithFocal") {
    element("midPoint", IMidPointSerializer.descriptor)
    element<AstroPoint>("focal")
    element<Double>("orb")
  }

  override fun serialize(encoder: Encoder, value: IMidPointWithFocal) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, IMidPointSerializer, value)
      encodeSerializableElement(descriptor, 1, AstroPoint.serializer(), value.focal)
      encodeDoubleElement(descriptor, 2, value.orb)
    }
  }

  override fun deserialize(decoder: Decoder): IMidPointWithFocal {
    var midPoint : IMidPoint? = null
    var focal : AstroPoint? = null
    var orb : Double? = null
    decoder.decodeStructure(descriptor) {
      while(true) {
        when (val index = decodeElementIndex(descriptor)) {
          0 -> midPoint = decodeSerializableElement(descriptor, 0, IMidPointSerializer)
          1 -> focal = decodeSerializableElement(descriptor, 1, AstroPoint.serializer())
          2                            -> orb = decodeDoubleElement(descriptor, 2)
          CompositeDecoder.DECODE_DONE -> break
          else                         -> throw SerializationException("Unknown index $index")
        }
      }
    }
    return MidPointWithFocal(midPoint!! as MidPoint, focal!!, orb!!)
  }

}
