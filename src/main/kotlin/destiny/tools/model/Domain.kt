package destiny.tools.model

import destiny.tools.converters.EnumMapConverter
import destiny.tools.converters.ParserCommons

enum class Domain {
  /** 紫微斗數 */
  ZIWEI,

  /** 易經隨機起卦 */
  ICHING_RAND,

  /** 占星本命盤 */
  HOROSCOPE,

  /** 塔羅占卜 */
  TAROT,

  /** 籤詩 */
  CHANCE,
}

object DomainConverter : EnumMapConverter<Domain>("domain") {
  private fun readResolve(): Any = DomainConverter
  override fun getContext(map: Map<String, String>): Domain? {
    return ParserCommons.parseEnum(key, Domain::class.java, map)
  }
}
