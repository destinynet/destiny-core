/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import destiny.core.astrology.AstroPattern
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Element
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


class AstroPatternSerializers {

  @OptIn(ExperimentalSerializationApi::class)
  object GrandTrineSerializer : KSerializer<AstroPattern.GrandTrine> {
    override val descriptor = buildClassSerialDescriptor("GrandTrine") {
      element<Set<AstroPoint>>("points")
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
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
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

  @OptIn(ExperimentalSerializationApi::class)
  object KiteSerializer : KSerializer<AstroPattern.Kite> {
    override val descriptor = buildClassSerialDescriptor("Kite") {
      element<PointSignHouse>("head")
      element<Set<AstroPoint>>("wings")
      element<PointSignHouse>("tail")
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

  @OptIn(ExperimentalSerializationApi::class)
  object TSquaredSerializer : KSerializer<AstroPattern.TSquared> {
    override val descriptor = buildClassSerialDescriptor("TSquared") {
      element<Set<AstroPoint>>("oppoPoints")
      element<PointSignHouse>("squared")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.TSquared) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.oppoPoints)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.squared)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.TSquared {
      var oppoPoints = setOf<AstroPoint>()
      var squared: PointSignHouse? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> oppoPoints = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> squared = decodeSerializableElement(descriptor, 1, PointSignHouse.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.TSquared(oppoPoints, squared!!, score)
    }
  }

}
