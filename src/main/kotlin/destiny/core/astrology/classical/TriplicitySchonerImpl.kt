/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Element.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign
import java.io.Serializable

/**
 * Schoner Triplicity 實作
 * 參考表格 https://imgur.com/zHWZQ8L
 * 來自 https://altairastrology.wordpress.com/2008/04/18/a-closer-look-at-triplicity/
 * 此表格中， EARTH + NIGHT == 太陽
 *
 * 但其他資料都顯示為金星 , 故，這裡採用金星
 *
 * <pre>
 *     | 白天 | 夜晚 | 共管
 * -----------------------
 * 火象 | 太陽 | 木星 | 火星
 * 土象 | 月亮 | 金星*| 土星
 * 風象 | 土星 | 水星 | 木星
 * 水象 | 金星 | 火星 | 月亮
 * </pre>
 */
class TriplicitySchonerImpl : ITriplicity, Serializable {

  /** 哪顆星在此星座得到三分相 (+3) */
  override fun ZodiacSign.getTriplicityPoint(dayNight: DayNight): Planet {
    return when(dayNight) {
      DayNight.DAY -> when(this.element) {
        FIRE -> SUN
        EARTH -> MOON
        AIR -> SATURN
        WATER -> VENUS
      }
      DayNight.NIGHT -> when(this.element) {
        FIRE -> JUPITER
        EARTH -> VENUS
        AIR -> MERCURY
        WATER -> MARS
      }
    }
  }

  /** 共管 , Partner */
  override fun ZodiacSign.getPartner(): Planet {
    return when (this.element) {
      FIRE -> MARS
      EARTH -> SATURN
      AIR -> JUPITER
      WATER -> MOON
    }
  }
}
