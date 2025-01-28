/**
 * Created by smallufo on 2025-01-25.
 */
package destiny.tools.serializers

import destiny.core.astrology.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*


object IMidPointSerializer : KSerializer<IMidPoint> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IMidPoint") {
    element<Set<AstroPoint>>("points")
    element<IZodiacDegree>("zodiacDegree")
    element<Int>("house")
  }


  override fun serialize(encoder: Encoder, value: IMidPoint) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
      encodeSerializableElement(descriptor, 1, IZodiacDegreeSerializer, value.degree)
      encodeIntElement(descriptor, 2, value.house)
    }
  }

  override fun deserialize(decoder: Decoder): IMidPoint {
    var points = setOf<AstroPoint>()
    var zodiacDegree: IZodiacDegree = 0.toZodiacDegree()
    var house = 0
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
          1                            -> zodiacDegree = decodeSerializableElement(descriptor, 1, IZodiacDegreeSerializer)
          2                            -> house = decodeIntElement(descriptor, 2)
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return MidPoint(points, zodiacDegree as ZodiacDegree, house)
  }
}
