/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:51:44
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.AverageDailyMotionMap

/** Swift in motion (faster than average).  */
class Swift : Rule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf { dailyDeg ->
      return@takeIf h.getPosition(planet)?.speedLng?.let { speedLng ->
        speedLng > dailyDeg
      }?:false
    }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
