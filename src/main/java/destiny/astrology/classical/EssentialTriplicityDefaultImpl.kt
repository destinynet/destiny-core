/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:28:19
 */
package destiny.astrology.classical

import destiny.astrology.*
import java.io.Serializable

/**
 * Essential Triplicity 實作 , 參考 Ptolemy's Table : <br></br>
 * <pre>
 * | 白天 | 夜晚
 * -------------------
 * 火相 | 太陽 | 木星
 * 土相 | 金星 | 月亮
 * 風相 | 土星 | 水星
 * 水相 | 火星 | 火星
</pre> *
 */
class EssentialTriplicityDefaultImpl : IEssentialTriplicity, Serializable {

  /** 取得黃道帶上某星座，其 Triplicity 是什麼星   */
  override fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point {
    return when (dayNight) {
      DayNight.DAY -> dayMap[sign.element]!!
      DayNight.NIGHT -> nightMap[sign.element]!!
    }
  }

  companion object {
    private val dayMap = mapOf(
      Element.FIRE to Planet.SUN,
      Element.EARTH to Planet.VENUS,
      Element.AIR to Planet.SATURN,
      Element.WATER to Planet.MARS
    )

    private val nightMap = mapOf(
      Element.FIRE to Planet.JUPITER ,
      Element.EARTH to Planet.MOON ,
      Element.AIR to Planet.MERCURY ,
      Element.WATER to Planet.MARS
    )
  }
}
