package destiny.tools.serializers.astrology

import destiny.core.astrology.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

object StarSerializer : KSerializer<Star> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Star", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Star) {
    when (value) {
      is Planet       -> PlanetSerializer.serialize(encoder, value)
      is Asteroid     -> AsteroidSerializer.serialize(encoder, value)
      is FixedStar    -> FixedStarSerializer.serialize(encoder, value)
      is LunarPoint   -> LunarPointSerializer.serialize(encoder, value)
      is Hamburger    -> HamburgerSerializer.serialize(encoder, value)
      is Arabic       -> ArabicSerializer.serialize(encoder, value)
      is LunarStation -> LunarStationSerializer.serialize(encoder, value)
    }
  }

  override fun deserialize(decoder: Decoder): Star {
    val value = decoder.decodeString()
    val wrappedValue = "\"$value\""
    return when {
      value.startsWith(Planet::class.simpleName!!)                      -> Json.decodeFromString(PlanetSerializer, wrappedValue)
      value.startsWith(Asteroid::class.simpleName!!)                    -> Json.decodeFromString(AsteroidSerializer, wrappedValue)
      value.startsWith(FixedStar::class.simpleName!!)                   -> Json.decodeFromString(FixedStarSerializer, wrappedValue)
      value.startsWith("LunarNode.") || value.startsWith("LunarApsis.") -> Json.decodeFromString(LunarPointSerializer, wrappedValue)
      value.startsWith(Hamburger::class.simpleName!!)                   -> Json.decodeFromString(HamburgerSerializer, wrappedValue)
      value.startsWith(Arabic::class.simpleName!!)                      -> Json.decodeFromString(ArabicSerializer, wrappedValue)
      else                                                              -> Json.decodeFromString(LunarStationSerializer, wrappedValue)
    }
  }
}
