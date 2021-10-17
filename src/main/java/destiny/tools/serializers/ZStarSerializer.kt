/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.tools.serializers

import destiny.core.chinese.ziwei.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object ZStarSerializer : KSerializer<ZStar> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZStar", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ZStar) {
    when (value) {
      is StarMain         -> encoder.encodeString(StarMain::class.simpleName + ":" + value.nameKey)
      is StarMinor        -> encoder.encodeString(StarMinor::class.simpleName + ":" + value.nameKey)
      is StarLucky        -> encoder.encodeString(StarLucky::class.simpleName + ":" + value.nameKey)
      is StarUnlucky      -> encoder.encodeString(StarUnlucky::class.simpleName + ":" + value.nameKey)

      is StarDoctor       -> encoder.encodeString(StarDoctor::class.simpleName + ":" + value.nameKey)
      is StarGeneralFront -> encoder.encodeString(StarGeneralFront::class.simpleName + ":" + value.nameKey)
      is StarLongevity    -> encoder.encodeString(StarLongevity::class.simpleName + ":" + value.nameKey)
      is StarYearFront    -> encoder.encodeString(StarYearFront::class.simpleName + ":" + value.nameKey)
    }
  }

  override fun deserialize(decoder: Decoder): ZStar {
    val raw = decoder.decodeString()

    val value = raw.substringAfter(":")

    return if (raw.startsWith(StarMain::class.simpleName!!)) {
      StarMain.fromString(value)!!
    } else if (raw.startsWith(StarMinor::class.simpleName!!)) {
      StarMinor.fromString(value)!!
    } else if (raw.startsWith(StarLucky::class.simpleName!!)) {
      StarLucky.fromString(value)!!
    } else if (raw.startsWith(StarUnlucky::class.simpleName!!)) {
      StarUnlucky.fromString(value)!!
    } else if (raw.startsWith(StarDoctor::class.simpleName!!)) {
      StarDoctor.fromString(value)!!
    } else if (raw.startsWith(StarGeneralFront::class.simpleName!!)) {
      StarGeneralFront.fromString(value)!!
    } else if (raw.startsWith(StarLongevity::class.simpleName!!)) {
      StarLongevity.fromString(value)!!
    } else if (raw.startsWith(StarYearFront::class.simpleName!!)) {
      StarYearFront.fromString(value)!!
    } else {
      error("unsupported : $raw")
    }
  }
}
