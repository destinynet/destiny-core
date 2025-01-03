package destiny.tools.ai.model

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

  sealed class Bdnp : Domain(true) {
    /** 八字 */
    data object EW : Bdnp()

    /** 紫微斗數 */
    data object ZIWEI : Bdnp()

    /** 占星本命盤 */
    data object HOROSCOPE : Bdnp()
  }


  /** 易經隨機起卦 */
  data object ICHING_RAND : Domain(false)

  /** 塔羅占卜 */
  data object TAROT : Domain(false)

  /** 籤詩 */
  data object CHANCE : Domain(false)

  /** 風水 , 地圖空拍版 */
  data object FENGSHUI_AERIAL : Domain(false)

  /** 占星骰子 */
  sealed class AstroDice : Domain(false) {
    data object ASTRO_DICE_SIMPLE : AstroDice()
    data object ASTRO_DICE_ADVANCED : AstroDice()
  }

  /** 紫微占卜 */
  data object ZIWEI_DIVINE : Domain(false)

  sealed class Daily : Domain(true) {
    /** 占星每日運勢 */
    data object DAILY_HOROSCOPE : Daily()
  }
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
      DomainObjectFinder.findDomainObject(s)
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
      DomainObjectFinder.findDomainObject(s)!!
    }
  }
}

