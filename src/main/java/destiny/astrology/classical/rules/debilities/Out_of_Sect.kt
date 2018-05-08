/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:46:51
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*
import destiny.astrology.Planet.*
import java.util.*

/**
 * 判斷不得時 (Out of sect) : 白天 , 夜星位於地平面上，落入陽性星座；或是晚上，晝星在地平面上，落入陰性星座
 * 晝星 : 日 , 木 , 土
 * 夜星 : 月 , 金 , 火
 *
 * 相對於不得時的，是「得時」 [destiny.astrology.classical.rules.accidentalDignities.Hayz]
 */
class Out_of_Sect(
  /** 計算白天黑夜的實作  */
  val dayNightImpl: DayNightDifferentiator) : Rule() {

  override fun getResult(planet: Planet, h: IHoro): Pair<String, Array<Any>>? {
    val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)

    val sign: ZodiacSign? = h.getZodiacSign(planet)
    val house: Int? = h.getHouse(planet)

    if (sign != null && house != null) {
      when (dayNight) {
        DayNight.DAY -> if (arrayOf(MOON, VENUS, MARS).contains(planet) && house >= 7 && sign.booleanValue) {
          logger.debug("夜星 {} 於白天在地平面上，落入陽性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
          return "commentNight" to arrayOf(planet, sign)
        }
        DayNight.NIGHT -> if (arrayOf(SUN , JUPITER , SATURN).contains(planet) && house >= 7 && !sign.booleanValue) {
          logger.debug("晝星 {} 於夜晚在地平面上，落入陰性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
          return "commentDay" to arrayOf(planet , sign)
        }
      }
    }
    return null
  }
}
