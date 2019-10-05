/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:04:45
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.core.DayNight
import java.util.*

/**
 * 判斷得時 (Hayz) : 白天 , 晝星位於地平面上，落入陽性星座；或是晚上，夜星在地平面上，落入陰性星座
 * 晝星 : 日 , 木 , 土
 * 夜星 : 月 , 金 , 火
 *
 * 相對於得時的，是「不得時」(out of sect)
 */
class Hayz(
  /** 計算白天黑夜的實作  */
  val dayNightImpl: IDayNight) : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)

    return h.getZodiacSign(planet)?.let { sign ->
      h.getHouse(planet)?.let { house ->
        when (dayNight) {
          DayNight.DAY -> planet.takeIf { arrayOf(SUN, JUPITER, SATURN).contains(it) }
            ?.takeIf { house >= 7 && sign.booleanValue }
            ?.let {
              logger.debug("晝星 {} 於白天在地平面上，落入陽性星座 {} , 得時", planet, sign.toString(Locale.TAIWAN))
              "commentDay" to arrayOf(planet, sign)
            }
          DayNight.NIGHT -> planet.takeIf { arrayOf(MOON, VENUS, MARS).contains(it) }
            ?.takeIf { house >= 7 && !sign.booleanValue }
            ?.let {
              logger.debug("夜星 {} 於夜晚在地平面上，落入陰性星座 {} , 得時", planet, sign.toString(Locale.TAIWAN))
              "commentNight" to arrayOf(planet, sign)
            }
        }
      }
    }

  }
}
