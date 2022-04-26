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


object AstroPointSerializer : KSerializer<AstroPoint> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AstroPoint" , PrimitiveKind.STRING)


  override fun serialize(encoder: Encoder, value: AstroPoint) {
    if (value is LunarApsis) {
      encoder.encodeString(LunarApsis::class.simpleName!!+"."+value::class.simpleName!!)
    } else {
      encoder.encodeString(value.nameKey)
    }
  }

  override fun deserialize(decoder: Decoder): AstroPoint {
    val raw = decoder.decodeString()
    return if (raw.startsWith(Planet::class.simpleName!!)) {
      Planet.fromString(raw.substringAfter("."))!!
    }
    else if (raw.startsWith(Asteroid::class.simpleName!!)) {
      Asteroid.fromString(raw.substringAfter("."))!!
    }
    else if (raw.startsWith("Fixed")) {
      FixedStar.fromString(raw.substringAfter("."))!!
    }
    else if (raw.startsWith(Hamburger::class.simpleName!!)) {
      Hamburger.fromString(raw.substringAfter("."))!!
    }
    else if (raw.startsWith(LunarApsis::class.simpleName!!)) {
      LunarApsis.fromString(raw.substringAfter("."))!!
    }
    else {
      error("unsupported : $raw")
    }
  }


}
