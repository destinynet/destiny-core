/**
 * Created by smallufo on 2017-12-23.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Element.*
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign
import java.io.Serializable

/**
 * J.B. Morin Triplicity 實作
 * 參考表格 https://imgur.com/zHWZQ8L
 * 來自 https://altairastrology.wordpress.com/2008/04/18/a-closer-look-at-triplicity/
 *
 * 以上表格的 水象 + 夜間 ==> 火星 , 但是其他資料顯示為月亮
 * 例如 https://tonylouis.wordpress.com/2012/04/03/triplicity-the-third-essential-dignity/
 *
 *
 * Morin 規則 :
 * Here are Morin’s guidelines:
 * 1. Day rulers have rulership by both domicile and exaltation in the triplicity.
 *    這三個星座，有 RULER 也有 EXALT 者，為 Day Ruler
 *    [火] 寅、午(日R)、戌(日E) ==> 日
 *    [土] 巳(水R)、酉、丑      ==> 水 (WHY?) , 只有一個符合啊
 *    [風] 申、子(土R)、辰(土E) ==> 土
 *    [水] 亥(木R)、卯、未(木E) ==> 木
 * 2. Night rulers have rulership of the cardinal signs of the triplicity.
 *    基本宮(辰戌丑未) 的 ruler 為 night ruler
 * 3. Finally, the participating ruler is the planet that is not exalted in the triplicity
 *    and does not rule the cardinal sign of the triplicity but does rule one of the three signs of the triplicity.
 *
 * <pre>
 *     | 白天 | 夜晚 | 共管
 * -----------------------
 * 火象 | 太陽 | 火星 | 木星
 * 土象 | 水星 | 土星 | 金星
 * 風象 | 土星 | 金星 | 水星
 * 水象 | 木星 | 月亮 | 火星
 *
 * </pre>
 */
class TriplicityMorinImpl : ITriplicity, Serializable {

  /** 哪顆星在此星座得到三分相 (+3) */
  override fun getPoint(sign: ZodiacSign, dayNight: DayNight): Planet {
    return when (dayNight) {
      DayNight.DAY -> dayMap[sign.element]!!
      DayNight.NIGHT -> nightMap[sign.element]!!
    }
  }

  /** 共管 , Partner */
  override fun getPartner(sign: ZodiacSign): Planet? {
    return when(sign.element) {
      FIRE -> JUPITER
      EARTH -> VENUS
      AIR -> MERCURY
      WATER -> MARS
    }
  }

  companion object {
    internal val dayMap = mapOf(
      FIRE to SUN ,
      EARTH to MERCURY ,
      AIR to SATURN,
      WATER to JUPITER
    )

    internal val nightMap = mapOf(
      FIRE to MARS ,
      EARTH to SATURN,
      AIR to VENUS,
      WATER to MOON
    )
  }
}