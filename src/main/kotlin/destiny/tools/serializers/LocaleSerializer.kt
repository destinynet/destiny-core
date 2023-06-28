package destiny.tools.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object LocaleSerializer : KSerializer<Locale> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("locale", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Locale) {

    encoder.encodeString(value.toLanguageTag())
  }

  override fun deserialize(decoder: Decoder): Locale {
    val str = decoder.decodeString().replace("\"","")
    return Locale.forLanguageTag(str)
  }

}
