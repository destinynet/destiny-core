package destiny.tools.serializers

import destiny.core.astrology.*
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@OptIn(ExperimentalSerializationApi::class)
object IPointAspectPatternSerializer : KSerializer<IPointAspectPattern> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IPointAspectPattern") {
    element("points", ListSerializer(AstroPointSerializer).descriptor)
    element<Double>("angle")
    element<Aspect>("aspect")
    element<IPointAspectPattern.Type>("type", isOptional = true)
    element<Double>("orb")
    element<Double>("score", isOptional = true)
  }


  override fun serialize(encoder: Encoder, value: IPointAspectPattern) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ListSerializer(AstroPointSerializer), value.points)
      encodeDoubleElement(descriptor, 1, value.angle)
      encodeSerializableElement(descriptor, 2, Aspect.serializer(), value.aspect)
      encodeNullableSerializableElement(descriptor, 3, IPointAspectPattern.Type.serializer(), value.type)
      encodeDoubleElement(descriptor, 4, value.orb)
      encodeNullableSerializableElement(descriptor, 5, Double.serializer(), value.score?.value)
    }
  }

  override fun deserialize(decoder: Decoder): IPointAspectPattern {
    var points: List<AstroPoint> = emptyList()
    var angle = 0.0
    var aspect: Aspect? = null
    var type: IPointAspectPattern.Type? = null
    var orb: Double = 0.0
    var score: Score? = null

    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> points = decodeSerializableElement(descriptor, 0, ListSerializer(AstroPointSerializer))
          1                            -> angle = decodeDoubleElement(descriptor, 1)
          2                            -> aspect = decodeSerializableElement(descriptor, 2, Aspect.serializer())
          3                            -> type = decodeNullableSerializableElement(descriptor, 3, IPointAspectPattern.Type.serializer())
          4                            -> orb = decodeDoubleElement(descriptor, 4)
          5                            -> score = decodeNullableSerializableElement(descriptor, 5, Double.serializer())?.toScore()
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    val p1 = points[0]
    val p2 = points[1]


    return PointAspectPattern(sortedSetOf(AstroPointComparator, p1, p2).toList(), angle, type, orb, score)
  }
}
