/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import destiny.core.astrology.*
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
class AstroPatternSerializers {

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

  object YodSerializer : KSerializer<AstroPattern.Yod> {
    override val descriptor = buildClassSerialDescriptor("Yod") {
      element<Set<AstroPoint>>("bottoms")
      element<PointSignHouse>("pointer")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.Yod) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.bottoms)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.pointer)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.Yod {
      var bottoms = setOf<AstroPoint>()
      var pointer: PointSignHouse? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> bottoms = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> pointer = decodeSerializableElement(descriptor, 1, PointSignHouse.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.Yod(bottoms, pointer!!, score)
    }
  }

  object BoomerangSerializer : KSerializer<AstroPattern.Boomerang> {
    override val descriptor = buildClassSerialDescriptor("Boomerang") {
      element("yod", AstroPattern.Yod.serializer().descriptor)
      element("oppoPoint", PointSignHouse.serializer().descriptor)
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.Boomerang) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, AstroPattern.Yod.serializer(), value.yod)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.oppoPoint)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.Boomerang {
      var yod: AstroPattern.Yod? = null
      var oppoPoint: PointSignHouse? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> yod = decodeSerializableElement(descriptor, 0, AstroPattern.Yod.serializer())
            1                            -> oppoPoint = decodeSerializableElement(descriptor, 1, PointSignHouse.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.Boomerang(yod!!, oppoPoint!!, score)
    }
  }

  object GoldenYodSerializer : KSerializer<AstroPattern.GoldenYod> {
    override val descriptor = buildClassSerialDescriptor("GoldenYod") {
      element<Set<AstroPoint>>("bottoms")
      element<PointSignHouse>("pointer")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.GoldenYod) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.bottoms)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.pointer)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.GoldenYod {
      var bottoms = setOf<AstroPoint>()
      var pointer: PointSignHouse? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> bottoms = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> pointer = decodeSerializableElement(descriptor, 1, PointSignHouse.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.GoldenYod(bottoms, pointer!!, score)
    }
  }

  object GrandCrossSerializer : KSerializer<AstroPattern.GrandCross> {
    override val descriptor = buildClassSerialDescriptor("GrandCross") {
      element<Set<AstroPoint>>("points")
      element<Quality>("quality")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.GrandCross) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeSerializableElement(descriptor, 1, Quality.serializer(), value.quality)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.GrandCross {
      var points = setOf<AstroPoint>()
      var quality: Quality? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> quality = decodeSerializableElement(descriptor, 1, Quality.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.GrandCross(points, quality!!, score)
    }
  }

  object DoubleTSerializer : KSerializer<AstroPattern.DoubleT> {
    override val descriptor = buildClassSerialDescriptor("DoubleT") {
      element<Set<AstroPattern.TSquared>>("tSquares")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.DoubleT) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.TSquared.serializer()), value.tSquares)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.DoubleT {
      var tSquares = setOf<AstroPattern.TSquared>()
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> tSquares = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.TSquared.serializer()))
            1                            -> score = decodeNullableSerializableElement(descriptor, 1, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.DoubleT(tSquares, score)
    }
  }

  object HexagonSerializer : KSerializer<AstroPattern.Hexagon> {
    override val descriptor = buildClassSerialDescriptor("Hexagon") {
      element<Set<AstroPattern.GrandTrine>>("grandTrines")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.Hexagon) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.GrandTrine.serializer()), value.grandTrines)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.Hexagon {
      var grandTrines = setOf<AstroPattern.GrandTrine>()
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> grandTrines = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.GrandTrine.serializer()))
            1                            -> score = decodeNullableSerializableElement(descriptor, 1, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.Hexagon(grandTrines, score)
    }
  }

  object WedgeSerializer : KSerializer<AstroPattern.Wedge> {
    override val descriptor = buildClassSerialDescriptor("Wedge") {
      element<Set<AstroPoint>>("oppoPoints")
      element("moderator", PointSignHouse.serializer().descriptor)
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.Wedge) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.oppoPoints)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.moderator)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.Wedge {
      var oppoPoints = setOf<AstroPoint>()
      var moderator: PointSignHouse? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> oppoPoints = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> moderator = decodeSerializableElement(descriptor, 1, PointSignHouse.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.Wedge(oppoPoints, moderator!!, score)
    }
  }

  object MysticRectangleSerializer : KSerializer<AstroPattern.MysticRectangle> {
    override val descriptor = buildClassSerialDescriptor("MysticRectangle") {
      element<Set<AstroPoint>>("points")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.MysticRectangle) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.MysticRectangle {
      var points = setOf<AstroPoint>()
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> score = decodeNullableSerializableElement(descriptor, 1, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.MysticRectangle(points, score)
    }
  }

  object PentagramSerializer : KSerializer<AstroPattern.Pentagram> {
    override val descriptor = buildClassSerialDescriptor("Pentagram") {
      element<Set<AstroPoint>>("points")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern.Pentagram) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): AstroPattern.Pentagram {
      var points = setOf<AstroPoint>()
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> score = decodeNullableSerializableElement(descriptor, 1, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return AstroPattern.Pentagram(points, score)
    }
  }

}
