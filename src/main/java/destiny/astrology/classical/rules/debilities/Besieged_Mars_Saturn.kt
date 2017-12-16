/**
 * @author smallufo
 * Created on 2007/12/31 at 上午 3:33:43
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.IBesieged
import destiny.astrology.Planet
import destiny.astrology.Planet.*
import destiny.core.calendar.TimeTools

/**
 * Besieged between Mars and Saturn.
 * 被火土夾制，只有日月水金，這四星有可能發生
 * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
 * 中間不能與其他行星形成角度
 */
class Besieged_Mars_Saturn(
  /** 計算兩星夾角的工具箱  */
  private val besiegedImpl: IBesieged) : Rule() {


  override fun getResult(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return planet.takeIf { arrayOf(SUN , MOON , MERCURY , VENUS).contains(it) }
      ?.takeIf {
        val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
        //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
        besiegedImpl.isBesieged(it , MARS , SATURN , gmt , true , true)
      }?.let { "comment" to arrayOf<Any>(planet , MARS , SATURN) }
  }
}
