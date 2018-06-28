/**
 * @author smallufo
 * Created on 2008/1/3 at 上午 8:55:07
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IBesieged
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.core.calendar.TimeTools

/**
 * 夾輔 : 被金星木星包夾 , 是很幸運的情形<br></br>
 * 角度考量 0/60/90/120/180 <br></br>
 * 中間不能與其他行星形成角度
 */
class Besieged_Jupiter_Venus(

  /** 計算兩星夾角的實作  */
  private val besiegedImpl: IBesieged) : AccidentalRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return planet.takeIf { arrayOf(SUN , MOON , MERCURY , MARS , SATURN).contains(it) }?.takeIf {
      val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
      besiegedImpl.isBesieged(it , VENUS , JUPITER , gmt , true , false)
    }?.let { "comment" to arrayOf<Any>(planet , VENUS , JUPITER) }
  }
}
