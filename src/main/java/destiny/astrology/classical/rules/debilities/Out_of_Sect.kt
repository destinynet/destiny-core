/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:46:51
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.*
import destiny.astrology.Planet.*
import destiny.core.DayNight
import java.util.*

/**
 * 判斷不得時 (Out of sect) : 白天 , 夜星位於地平面上，落入陽性星座；或是晚上，晝星在地平面上，落入陰性星座
 * 晝星 : 日 , 木 , 土
 * 夜星 : 月 , 金 , 火
 *
 * 相對於不得時的，是「得時」 (hayz)
 */
class Out_of_Sect(
  /** 計算白天黑夜的實作  */
  val dayNightImpl: IDayNight) : DebilityRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)

    val sign: ZodiacSign? = h.getZodiacSign(planet)
    val house: Int? = h.getHouse(planet)

    return sign?.let { _ ->
      house?.let { _ ->
        when (dayNight) {
          DayNight.DAY -> if (arrayOf(MOON, VENUS, MARS).contains(planet) && house >= 7 && sign.booleanValue) {
            logger.debug("夜星 {} 於白天在地平面上，落入陽性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
            "commentNight" to arrayOf(planet, sign)
          } else {
            null
          }
          DayNight.NIGHT -> if (arrayOf(SUN, JUPITER, SATURN).contains(planet) && house >= 7 && !sign.booleanValue) {
            logger.debug("晝星 {} 於夜晚在地平面上，落入陰性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
            "commentDay" to arrayOf(planet , sign)
          } else {
            null
          }
        }
      }
    }
  }
}
