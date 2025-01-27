/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import destiny.core.astrology.AstroPattern
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.PointSignHouse
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
object KiteSerializer : KSerializer<AstroPattern.Kite> {
  override val descriptor = buildClassSerialDescriptor("Kite") {
    element("head", PointSignHouse.serializer().descriptor)
    element("wings", SetSerializer(AstroPoint.serializer()).descriptor)
    element("tail", PointSignHouse.serializer().descriptor)
    element<Double>("score", isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: AstroPattern.Kite) {
    encoder.encodeStructure(descriptor) {
      encodeSerializableElement(descriptor, 0, PointSignHouse.serializer(), value.head)
      encodeSerializableElement(descriptor, 1, SetSerializer(AstroPoint.serializer()), value.wings)
      encodeSerializableElement(descriptor, 2, PointSignHouse.serializer(), value.tail)
      encodeNullableSerializableElement(descriptor, 3, Double.serializer(), value.score?.value)
    }
  }

  override fun deserialize(decoder: Decoder): AstroPattern.Kite {
    var head: PointSignHouse? = null
    var wings = setOf<AstroPoint>()
    var tail: PointSignHouse? = null
    var score: Score? = null
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0                            -> head = decodeSerializableElement(descriptor, 0, PointSignHouse.serializer())
          1                            -> wings = decodeSerializableElement(descriptor, 1, SetSerializer(AstroPoint.serializer()))
          2                            -> tail = decodeSerializableElement(descriptor, 2, PointSignHouse.serializer())
          3                            -> score = decodeNullableSerializableElement(descriptor, 3, Double.serializer())?.toScore()
          CompositeDecoder.DECODE_DONE -> break
          else                         -> error("Unexpected index: $index")
        }
      }
    }
    return AstroPattern.Kite(head!!, wings, tail!!, score)
  }
}
