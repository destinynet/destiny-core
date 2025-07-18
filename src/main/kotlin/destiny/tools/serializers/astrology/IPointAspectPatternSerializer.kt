package destiny.tools.serializers.astrology

import destiny.core.astrology.*
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ScoreTwoDecimalSerializer
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
    element("points", ListSerializer(AstroPoint.serializer()).descriptor)
    element<Double>("angle")
    element<Aspect>("aspect")
    element<IPointAspectPattern.AspectType>("type", isOptional = true)
    element<Double>("orb")
    element<Double>("score", isOptional = true)
  }


  override fun serialize(encoder: Encoder, value: IPointAspectPattern) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, ListSerializer(AstroPoint.serializer()), value.points)
      encodeDoubleElement(descriptor, 1, value.angle)
      encodeSerializableElement(descriptor, 2, Aspect.serializer(), value.aspect)
      encodeNullableSerializableElement(descriptor, 3, IPointAspectPattern.AspectType.serializer(), value.aspectType)
//      encodeDoubleElement(descriptor, 4, value.orb)
      encodeNullableSerializableElement(descriptor, 4, DoubleTwoDecimalSerializer, value.orb)
//      encodeNullableSerializableElement(descriptor, 5, Double.serializer(), value.score?.value)
      encodeNullableSerializableElement(descriptor, 5, ScoreTwoDecimalSerializer, value.score)
    }
  }

  override fun deserialize(decoder: Decoder): IPointAspectPattern {
    var points: List<AstroPoint> = emptyList()
    var angle = 0.0
    var aspect: Aspect? = null
    var aspectType: IPointAspectPattern.AspectType? = null
    var orb = 0.0
    var score: Score? = null

    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> points = decodeSerializableElement(descriptor, 0, ListSerializer(AstroPoint.serializer()))
          1                            -> angle = decodeDoubleElement(descriptor, 1)
          2                            -> aspect = decodeSerializableElement(descriptor, 2, Aspect.serializer())
          3                            -> aspectType = decodeNullableSerializableElement(descriptor, 3, IPointAspectPattern.AspectType.serializer())
          4                            -> orb = decodeDoubleElement(descriptor, 4)
          5                            -> score = decodeNullableSerializableElement(descriptor, 5, Double.serializer())?.toScore()
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    val p1 = points[0]
    val p2 = points[1]


    return PointAspectPattern(sortedSetOf(AstroPointComparator, p1, p2).toList(), angle, aspectType, orb, score)
  }
}
