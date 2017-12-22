/**
 * Created by smallufo on 2017-12-22.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Element.*
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign
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
class TriplicitySchonerImpl : ITriplicity , Serializable {

  /** 哪顆星在此星座得到三分相 (+3) */
  override fun getPoint(sign: ZodiacSign, dayNight: DayNight): Planet {
    return when(dayNight) {
      DayNight.DAY -> dayMap[sign.element]!!
      DayNight.NIGHT -> nightMap[sign.element]!!
    }
  }

  /** 共管 , Partner */
  override fun getPartner(sign: ZodiacSign): Planet? {
    return when (sign.element) {
      FIRE -> MARS
      EARTH -> SATURN
      AIR -> JUPITER
      WATER -> MOON
    }
  }

  companion object {
    internal val dayMap = mapOf(
      FIRE to SUN ,
      EARTH to MOON ,
      AIR to SATURN ,
      WATER to VENUS
    )

    internal val nightMap = mapOf(
      FIRE to JUPITER ,
      EARTH to VENUS ,
      AIR to MERCURY ,
      WATER to MARS
    )
  }

}