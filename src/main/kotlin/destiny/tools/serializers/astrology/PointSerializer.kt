/**
 * Created by smallufo on 2025-01-27.
 */
package destiny.tools.serializers.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.core.astrology.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json


abstract class PointSerializer<T : Point>(
  private val points: IPoints<T>,
  private val typeName: String = points.type.simpleName!!
) : KSerializer<T> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(typeName, PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: T) {
    encoder.encodeString(value.nameKey)
  }

  override fun deserialize(decoder: Decoder): T {
    val value = decoder.decodeString()
    return points.valueOf(value)
      ?: error("Invalid $typeName nameKey: '$value'. Valid values are: ${points.values.map { it.nameKey }}")
  }
}

object PlanetSerializer : PointSerializer<Planet>(Planet)
object AsteroidSerializer : PointSerializer<Asteroid>(Asteroid)
object FixedStarSerializer : PointSerializer<FixedStar>(FixedStar)
object HamburgerSerializer : PointSerializer<Hamburger>(Hamburger)
object ArabicSerializer : PointSerializer<Arabic>(Arabic)
object LunarStationSerializer : PointSerializer<LunarStation>(LunarStation)

object LunarNodeSerializer : PointSerializer<LunarNode>(LunarNode)
object LunarApsisSerializer : PointSerializer<LunarApsis>(LunarApsis) {
//  override fun serialize(encoder: Encoder, value: LunarApsis) {
//    encoder.encodeString(LunarApsis::class.simpleName!! + "." + value::class.simpleName!!)
//  }
//
//  override fun deserialize(decoder: Decoder): LunarApsis {
//    val raw = decoder.decodeString()
//    return LunarApsis.fromString(raw.substringAfter("."))!!
//  }
}

object LunarPointSerializer : KSerializer<LunarPoint> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LunarPoint", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LunarPoint) {
    when (value) {
      is LunarNode  -> LunarNodeSerializer.serialize(encoder, value)
      is LunarApsis -> LunarApsisSerializer.serialize(encoder, value)
      else          -> error("Unknown LunarPoint type: ${value::class.simpleName}")
    }
  }

  override fun deserialize(decoder: Decoder): LunarPoint {
    val value = decoder.decodeString()
    return when {
      value.startsWith("LunarNode.")  -> Json.decodeFromString(LunarNodeSerializer, "\"$value\"")
      value.startsWith("LunarApsis.") -> Json.decodeFromString(LunarApsisSerializer, "\"$value\"")
      else                            -> error("Invalid LunarPoint value: $value")
    }
  }
}

object AxisSerializer : PointSerializer<Axis>(Axis)
