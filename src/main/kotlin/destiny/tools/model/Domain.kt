package destiny.tools.model

import destiny.tools.converters.MapConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*


@Serializable(with = DomainSerializer::class)
sealed class Domain(val bdnpGenerated: Boolean) {

  /** 八字 */
  data object EW : Domain(true)

  /** 紫微斗數 */
  data object ZIWEI : Domain(true)

  /** 占星本命盤 */
  data object HOROSCOPE : Domain(true)

  /** 易經隨機起卦 */
  data object ICHING_RAND : Domain(false)

  /** 塔羅占卜 */
  data object TAROT : Domain(false)

  /** 籤詩 */
  data object CHANCE : Domain(false)
}

fun Domain.getTitle(locale: Locale): String {
  val resource = Domain::class.qualifiedName!!
  return ResourceBundle.getBundle(resource, locale).getString("$this.title")
}

object DomainConverter : MapConverter<Domain> {

  private fun readResolve(): Any = DomainConverter

  override val key: String = "domain"

  override fun getMap(context: Domain): Map<String, String> {
    return mapOf(key to context.toString())
  }

  override fun getContext(map: Map<String, String>): Domain? {
    return map[key]?.let { s ->
      Domain::class.sealedSubclasses.firstOrNull { it.simpleName == s }?.objectInstance
    }
  }
}

object DomainSerializer: KSerializer<Domain> {

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Domain", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Domain) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): Domain {
    return decoder.decodeString().let { s ->
      Domain::class.sealedSubclasses.firstOrNull { it.simpleName == s }?.objectInstance!!
    }
  }



}
