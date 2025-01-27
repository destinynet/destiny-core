/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.tools.serializers

import destiny.core.astrology.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json


object AstroPointSerializer : KSerializer<AstroPoint> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AstroPoint" , PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: AstroPoint) {
    encoder.encodeString(value.nameKey)
  }

  override fun deserialize(decoder: Decoder): AstroPoint {
    val value = decoder.decodeString()
    val wrappedValue = "\"$value\""

    return if (value.startsWith(Planet::class.simpleName!!)
      || value.startsWith(Asteroid::class.simpleName!!)
      || value.startsWith(FixedStar::class.simpleName!!)
      || value.startsWith(LunarNode::class.simpleName!!)
      || value.startsWith(LunarApsis::class.simpleName!!)
      || value.startsWith(Hamburger::class.simpleName!!)
      || value.startsWith(Arabic::class.simpleName!!)
      ) {
      Json.decodeFromString(StarSerializer, wrappedValue)
    } else if (value.startsWith(Axis::class.simpleName!!)) {
      Json.decodeFromString(AxisSerializer, wrappedValue)
    } else {
      Json.decodeFromString(LunarStationSerializer, wrappedValue)
    }
  }


}
