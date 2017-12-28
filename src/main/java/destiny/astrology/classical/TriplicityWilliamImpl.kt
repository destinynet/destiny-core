package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Element.*
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import java.io.Serializable

/**
 * https://altairastrology.wordpress.com/2008/04/18/a-closer-look-at-triplicity/
 *
 * Essential Triplicity 實作 , 參考 Ptolemy's Table :
 * <pre>
 *     | 白天 | 夜晚 | 共管
 * -----------------------
 * 火象 | 太陽 | 木星 |
 * 土象 | 金星 | 月亮 |
 * 風象 | 土星 | 水星 |
 * 水象 | 火星 | 火星 | 火星
 * </pre>
 * 這是 William Lily 的定義 , 尤其「水象星座」 日、夜，都是火星 (比較不合理)
 *
 * Lilly’s view is the same as Ptolemy’s [TriplicityPtolomyImpl] except that
 * Lilly makes Mars the day and night ruler of the watery triplicity and
 * ignores participating rulers.
 */
class TriplicityWilliamImpl : ITriplicity, Serializable {

  /** 哪顆星在此星座得到三分相 (+3) */
  override fun getPoint(sign: ZodiacSign, dayNight: DayNight): Planet {
    return when(dayNight) {
      DayNight.DAY -> when(sign.element) {
        FIRE -> SUN
        EARTH -> VENUS
        AIR -> SATURN
        WATER -> MARS
      }
      DayNight.NIGHT -> when(sign.element) {
        FIRE -> JUPITER
        EARTH -> MOON
        AIR -> MERCURY
        WATER -> MARS
      }
    }
  }

  /**
   * 共管 , Partner
   * Ptolomy 只有水象星座，由火星共管
   *  */
  override fun getPartner(sign: ZodiacSign): Point? {
    return sign.element.takeIf { it === WATER }?.let { MARS }
  }

//  companion object {
//    internal val dayMap = mapOf(
//      FIRE to SUN,
//      EARTH to VENUS,
//      AIR to SATURN,
//      WATER to MARS
//    )
//
//    internal val nightMap = mapOf(
//      FIRE to JUPITER,
//      EARTH to MOON,
//      AIR to MERCURY,
//      WATER to MARS
//    )
//  }
}