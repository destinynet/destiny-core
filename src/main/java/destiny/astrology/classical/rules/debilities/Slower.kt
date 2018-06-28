/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:08:31
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.AverageDailyMotionMap

class Slower : DebilityRule() {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>? {
    return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf{ dailyDeg ->
      h.getPosition(planet)?.speedLng?.let { speedLng ->
        speedLng < dailyDeg
      }?:false
    }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
