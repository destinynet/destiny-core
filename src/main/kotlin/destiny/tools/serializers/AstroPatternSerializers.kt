/**
 * Created by smallufo on 2025-01-28.
 */
package destiny.tools.serializers

import destiny.core.astrology.*
import destiny.core.astrology.AstroPattern.*
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


@OptIn(ExperimentalSerializationApi::class)
class AstroPatternSerializers {

  object GrandTrineSerializer : KSerializer<GrandTrine> {
    override val descriptor = buildClassSerialDescriptor("GrandTrine") {
      element<Set<AstroPoint>>("points")
      element<Element>("element")
      element<Double>("score", isOptional = true)
    }


    override fun serialize(encoder: Encoder, value: GrandTrine) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeSerializableElement(descriptor, 1, Element.serializer(), value.element)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): GrandTrine {
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
      return GrandTrine(points, element!!, score)
    }
  }

  object KiteSerializer : KSerializer<Kite> {
    override val descriptor = buildClassSerialDescriptor("Kite") {
      element<PointSignHouse>("head")
      element<Set<AstroPoint>>("wings")
      element<PointSignHouse>("tail")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Kite) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, PointSignHouse.serializer(), value.head)
        encodeSerializableElement(descriptor, 1, SetSerializer(AstroPoint.serializer()), value.wings)
        encodeSerializableElement(descriptor, 2, PointSignHouse.serializer(), value.tail)
        encodeNullableSerializableElement(descriptor, 3, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Kite {
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
      return Kite(head!!, wings, tail!!, score)
    }
  }

  object TSquaredSerializer : KSerializer<TSquared> {
    override val descriptor = buildClassSerialDescriptor("TSquared") {
      element<Set<AstroPoint>>("oppoPoints")
      element<PointSignHouse>("squared")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: TSquared) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.oppoPoints)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.squared)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): TSquared {
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
      return TSquared(oppoPoints, squared!!, score)
    }
  }

  object YodSerializer : KSerializer<Yod> {
    override val descriptor = buildClassSerialDescriptor("Yod") {
      element<Set<AstroPoint>>("bottoms")
      element<PointSignHouse>("pointer")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Yod) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.bottoms)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.pointer)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Yod {
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
      return Yod(bottoms, pointer!!, score)
    }
  }

  object BoomerangSerializer : KSerializer<Boomerang> {
    override val descriptor = buildClassSerialDescriptor("Boomerang") {
      element("yod", AstroPattern.Yod.serializer().descriptor)
      element("oppoPoint", PointSignHouse.serializer().descriptor)
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Boomerang) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, AstroPattern.Yod.serializer(), value.yod)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.oppoPoint)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Boomerang {
      var yod: Yod? = null
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
      return Boomerang(yod!!, oppoPoint!!, score)
    }
  }

  object GoldenYodSerializer : KSerializer<GoldenYod> {
    override val descriptor = buildClassSerialDescriptor("GoldenYod") {
      element<Set<AstroPoint>>("bottoms")
      element<PointSignHouse>("pointer")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: GoldenYod) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.bottoms)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.pointer)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): GoldenYod {
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
      return GoldenYod(bottoms, pointer!!, score)
    }
  }

  object GrandCrossSerializer : KSerializer<GrandCross> {
    override val descriptor = buildClassSerialDescriptor("GrandCross") {
      element<Set<AstroPoint>>("points")
      element<Quality>("quality")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: GrandCross) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeSerializableElement(descriptor, 1, Quality.serializer(), value.quality)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): GrandCross {
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
      return GrandCross(points, quality!!, score)
    }
  }

  object DoubleTSerializer : KSerializer<DoubleT> {
    override val descriptor = buildClassSerialDescriptor("DoubleT") {
      element<Set<TSquared>>("tSquares")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: DoubleT) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.TSquared.serializer()), value.tSquares)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): DoubleT {
      var tSquares = setOf<TSquared>()
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
      return DoubleT(tSquares, score)
    }
  }

  object HexagonSerializer : KSerializer<Hexagon> {
    override val descriptor = buildClassSerialDescriptor("Hexagon") {
      element<Set<GrandTrine>>("grandTrines")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Hexagon) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPattern.GrandTrine.serializer()), value.grandTrines)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Hexagon {
      var grandTrines = setOf<GrandTrine>()
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
      return Hexagon(grandTrines, score)
    }
  }

  object WedgeSerializer : KSerializer<Wedge> {
    override val descriptor = buildClassSerialDescriptor("Wedge") {
      element<Set<AstroPoint>>("oppoPoints")
      element("moderator", PointSignHouse.serializer().descriptor)
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Wedge) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.oppoPoints)
        encodeSerializableElement(descriptor, 1, PointSignHouse.serializer(), value.moderator)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Wedge {
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
      return Wedge(oppoPoints, moderator!!, score)
    }
  }

  object MysticRectangleSerializer : KSerializer<MysticRectangle> {
    override val descriptor = buildClassSerialDescriptor("MysticRectangle") {
      element<Set<AstroPoint>>("points")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: MysticRectangle) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): MysticRectangle {
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
      return MysticRectangle(points, score)
    }
  }

  object PentagramSerializer : KSerializer<Pentagram> {
    override val descriptor = buildClassSerialDescriptor("Pentagram") {
      element<Set<AstroPoint>>("points")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Pentagram) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Pentagram {
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
      return Pentagram(points, score)
    }
  }

  object StelliumSignSerializer : KSerializer<StelliumSign> {
    override val descriptor = buildClassSerialDescriptor("StelliumSign") {
      element<Set<AstroPoint>>("points")
      element("sign", ZodiacSign.serializer().descriptor)
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: StelliumSign) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeSerializableElement(descriptor, 1, ZodiacSign.serializer(), value.sign)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): StelliumSign {
      var points = setOf<AstroPoint>()
      var sign: ZodiacSign? = null
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> sign = decodeSerializableElement(descriptor, 1, ZodiacSign.serializer())
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return StelliumSign(points, sign!!, score)
    }
  }

  object StelliumHouseSerializer : KSerializer<StelliumHouse> {
    override val descriptor = buildClassSerialDescriptor("StelliumHouse") {
      element<Set<AstroPoint>>("points")
      element<Int>("house")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: StelliumHouse) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()), value.points)
        encodeIntElement(descriptor, 1, value.house)
        encodeNullableSerializableElement(descriptor, 2, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): StelliumHouse {
      var points = setOf<AstroPoint>()
      var house = 0
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> points = decodeSerializableElement(descriptor, 0, SetSerializer(AstroPoint.serializer()))
            1                            -> house = decodeIntElement(descriptor, 1)
            2                            -> score = decodeNullableSerializableElement(descriptor, 2, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return StelliumHouse(points, house, score)
    }
  }

  object ConfrontationSerializer : KSerializer<Confrontation> {
    override val descriptor = buildClassSerialDescriptor("Confrontation") {
      element<Set<Set<AstroPoint>>>("clusters")
      element<Double>("score", isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: Confrontation) {
      encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, SetSerializer(SetSerializer(AstroPoint.serializer())), value.clusters)
        encodeNullableSerializableElement(descriptor, 1, Double.serializer(), value.score?.value)
      }
    }

    override fun deserialize(decoder: Decoder): Confrontation {
      var clusters = setOf<Set<AstroPoint>>()
      var score: Score? = null
      decoder.decodeStructure(descriptor) {
        while (true) {
          when (val index = decodeElementIndex(descriptor)) {
            0                            -> clusters = decodeSerializableElement(descriptor, 0, SetSerializer(SetSerializer(AstroPoint.serializer())))
            1                            -> score = decodeNullableSerializableElement(descriptor, 1, Double.serializer())?.toScore()
            CompositeDecoder.DECODE_DONE -> break
            else                         -> error("Unexpected index: $index")
          }
        }
      }
      return Confrontation(clusters, score)
    }
  }

  object AstroPatternSerializer : KSerializer<AstroPattern> {
    private const val DISCRIMINATOR = "type"
    private const val CONTENT = "content"

    override val descriptor = buildClassSerialDescriptor("AstroPattern") {
      element<String>(DISCRIMINATOR)
      element<JsonElement>(CONTENT)
    }

    override fun serialize(encoder: Encoder, value: AstroPattern) {
      val compositeEncoder = encoder.beginStructure(descriptor)

      when (value) {
        is GrandTrine      -> {
          compositeEncoder.encodeStringElement(descriptor, 0, GrandTrine::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, GrandTrineSerializer, value)
        }

        is Kite            -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Kite::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, KiteSerializer, value)
        }

        is TSquared        -> {
          compositeEncoder.encodeStringElement(descriptor, 0, TSquared::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, TSquaredSerializer, value)
        }

        is Yod             -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Yod::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, YodSerializer, value)
        }

        is Boomerang       -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Boomerang::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, BoomerangSerializer, value)
        }

        is GoldenYod       -> {
          compositeEncoder.encodeStringElement(descriptor, 0, GoldenYod::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, GoldenYodSerializer, value)
        }

        is GrandCross      -> {
          compositeEncoder.encodeStringElement(descriptor, 0, GrandCross::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, GrandCrossSerializer, value)
        }

        is DoubleT         -> {
          compositeEncoder.encodeStringElement(descriptor, 0, DoubleT::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, DoubleTSerializer, value)
        }

        is Hexagon         -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Hexagon::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, HexagonSerializer, value)
        }

        is Wedge           -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Wedge::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, WedgeSerializer, value)
        }

        is MysticRectangle -> {
          compositeEncoder.encodeStringElement(descriptor, 0, MysticRectangle::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, MysticRectangleSerializer, value)
        }

        is Pentagram       -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Pentagram::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, PentagramSerializer, value)
        }

        is StelliumSign    -> {
          compositeEncoder.encodeStringElement(descriptor, 0, StelliumSign::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, StelliumSignSerializer, value)
        }

        is StelliumHouse   -> {
          compositeEncoder.encodeStringElement(descriptor, 0, StelliumHouse::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, StelliumHouseSerializer, value)
        }

        is Confrontation   -> {
          compositeEncoder.encodeStringElement(descriptor, 0, Confrontation::class.java.simpleName)
          compositeEncoder.encodeSerializableElement(descriptor, 1, ConfrontationSerializer, value)
        }
      }
      compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): AstroPattern {
      val compositeDecoder = decoder.beginStructure(descriptor)
      var type: String? = null
      var content: JsonElement? = null

      while (true) {
        when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
          CompositeDecoder.DECODE_DONE -> break
          0                            -> type = compositeDecoder.decodeStringElement(descriptor, 0)
          1                            -> content = compositeDecoder.decodeSerializableElement(descriptor, 1, JsonElement.serializer())
          else                         -> error("Unexpected index: $index")
        }
      }

      requireNotNull(type) { "Type discriminator should not be null" }
      requireNotNull(content) { "Content should not be null" }

      val result = when (type) {
        GrandTrine::class.java.simpleName      -> Json.decodeFromJsonElement(GrandTrineSerializer, content)
        Kite::class.java.simpleName            -> Json.decodeFromJsonElement(KiteSerializer, content)
        TSquared::class.java.simpleName        -> Json.decodeFromJsonElement(TSquaredSerializer, content)
        Yod::class.java.simpleName             -> Json.decodeFromJsonElement(YodSerializer, content)
        Boomerang::class.java.simpleName       -> Json.decodeFromJsonElement(BoomerangSerializer, content)
        GoldenYod::class.java.simpleName       -> Json.decodeFromJsonElement(GoldenYodSerializer, content)
        GrandCross::class.java.simpleName      -> Json.decodeFromJsonElement(GrandCrossSerializer, content)
        DoubleT::class.java.simpleName         -> Json.decodeFromJsonElement(DoubleTSerializer, content)
        Hexagon::class.java.simpleName         -> Json.decodeFromJsonElement(HexagonSerializer, content)
        Wedge::class.java.simpleName           -> Json.decodeFromJsonElement(WedgeSerializer, content)
        MysticRectangle::class.java.simpleName -> Json.decodeFromJsonElement(MysticRectangleSerializer, content)
        Pentagram::class.java.simpleName       -> Json.decodeFromJsonElement(PentagramSerializer, content)
        StelliumSign::class.java.simpleName    -> Json.decodeFromJsonElement(StelliumSignSerializer, content)
        StelliumHouse::class.java.simpleName   -> Json.decodeFromJsonElement(StelliumHouseSerializer, content)
        Confrontation::class.java.simpleName   -> Json.decodeFromJsonElement(ConfrontationSerializer, content)
        else                                   -> error("Unknown type: $type")
      }

      compositeDecoder.endStructure(descriptor)
      return result
    }
  }
}
