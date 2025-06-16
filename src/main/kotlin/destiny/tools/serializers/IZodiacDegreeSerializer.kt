/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers

import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacSign
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*


object IZodiacDegreeSerializer : KSerializer<IZodiacDegree> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IZodiacDegree") {
    element<ZodiacSign>("sign")
    element<Double>("degree")
  }

  override fun serialize(encoder: Encoder, value: IZodiacDegree) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ZodiacSign.serializer(), value.sign)
      encodeSerializableElement(descriptor, 1, DoubleTwoDecimalSerializer, value.signDegree.second)
    }
  }

  override fun deserialize(decoder: Decoder): IZodiacDegree {
    var sign: ZodiacSign? = null
    var degree = 0.0
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> sign = decodeSerializableElement(descriptor, 0, ZodiacSign.serializer())
          1                            -> degree = decodeDoubleElement(descriptor, 1)
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return ZodiacDegree.of(sign!!, degree)
  }

}
