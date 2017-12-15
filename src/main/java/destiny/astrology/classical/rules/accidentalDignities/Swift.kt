/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:51:44
 */
package destiny.astrology.classical.rules.accidentalDignities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.AverageDailyMotionMap
import org.jooq.lambda.tuple.Tuple2
import java.util.*

/** Swift in motion (faster than average).  */
class Swift : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return getResult2(planet , h).toOld()
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf { dailyDeg ->
      return@takeIf h.getPosition(planet)?.speedLng?.let { speedLng ->
        speedLng > dailyDeg
      }?:false
    }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
