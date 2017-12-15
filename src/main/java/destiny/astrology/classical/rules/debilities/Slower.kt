/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:08:31
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.AverageDailyMotionMap
import org.jooq.lambda.tuple.Tuple2
import java.util.*

class Slower : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf{ dailyDeg ->
      h.getPosition(planet)?.speedLng?.let { speedLng ->
        speedLng < dailyDeg
      }?:false
    }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
