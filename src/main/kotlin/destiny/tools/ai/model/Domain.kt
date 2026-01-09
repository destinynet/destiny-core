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
sealed class Domain {

  sealed class Bdnp : Domain() {
    /** 八字 */
    data object EW : Bdnp()

    /** 紫微斗數 */
    data object ZIWEI : Bdnp()

    /** 占星本命盤 */
    data object HOROSCOPE : Bdnp()
  }


  /** 易經隨機起卦 */
  data object ICHING_RAND : Domain()

  /** 塔羅占卜 */
  data object TAROT : Domain()

  /** 籤詩 */
  data object CHANCE : Domain()

  /** 風水 , 地圖空拍版 */
  data object FENGSHUI_AERIAL : Domain()

  /** 占星骰子 */
  sealed class AstroDice : Domain() {
    data object ASTRO_DICE_SIMPLE : AstroDice()
    data object ASTRO_DICE_ADVANCED : AstroDice()
  }

  /** 紫微占卜 */
  data object ZIWEI_DIVINE : Domain()

  /** 卜卦占星 */
  data object HORARY : Domain()

  /** 運勢 */
  sealed class Period : Domain() {
    /** 占星每日運勢 */
    data object DAILY_HOROSCOPE : Period()

    /** 占星每月運勢 */
    data object MONTHLY_HOROSCOPE : Period()
  }

  /** 合盤 */
  data object SYNASTRY : Domain()

  /** 擇日 */
  sealed class Electional : Domain() {
    data object ELECTIONAL_YEAR_MONTH : Electional()
    data object ELECTIONAL_DAY_HOUR : Electional()
  }

  /** 連結到個人命盤 */
  sealed class BirthData : Domain() {
    /** 個人星盤 : 占星 */
    data object BIRTHDATA_HOROSCOPE : BirthData()
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

