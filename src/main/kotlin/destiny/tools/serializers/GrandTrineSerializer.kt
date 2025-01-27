/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import destiny.core.astrology.AstroPattern
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Element
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*


@OptIn(ExperimentalSerializationApi::class)
object GrandTrineSerializer : KSerializer<AstroPattern.GrandTrine> {
  override val descriptor = buildClassSerialDescriptor("GrandTrine") {
    element("points", SetSerializer(AstroPoint.serializer()).descriptor)
    element<Element>("element")
    element<Double>("score", isOptional = true)
  }


  override fun serialize(encoder: Encoder, value: AstroPattern.GrandTrine) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
      encodeSerializableElement(descriptor, 1, Element.serializer(), value.element)
      encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
    }
  }

  override fun deserialize(decoder: Decoder): AstroPattern.GrandTrine {
    var points = setOf<AstroPoint>()
    var element: Element? = null
    var score: Score? = null
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPointSerializer))
          1                            -> element = decodeSerializableElement(descriptor, 1, Element.serializer())
          2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return AstroPattern.GrandTrine(points, element!!, score)
  }
}
