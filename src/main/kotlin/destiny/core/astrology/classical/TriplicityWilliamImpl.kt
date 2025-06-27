package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Element.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign
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
object TriplicityWilliamImpl : ITriplicity, Serializable {
  private fun readResolve(): Any = TriplicityWilliamImpl

  /** 哪顆星在此星座得到三分相 (+3) */
  override fun ZodiacSign.getTriplicityPoint(dayNight: DayNight): Planet {
    return when(dayNight) {
      DayNight.DAY -> when(this.element) {
        FIRE -> SUN
        EARTH -> VENUS
        AIR -> SATURN
        WATER -> MARS
      }
      DayNight.NIGHT -> when(this.element) {
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
  override fun ZodiacSign.getPartner(): AstroPoint? {
    return this.element.takeIf { it === WATER }?.let { MARS }
  }

}
