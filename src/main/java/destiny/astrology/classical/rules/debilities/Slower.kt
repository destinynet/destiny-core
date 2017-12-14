/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 5:08:31
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.AverageDailyMotionMap
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import java.util.*
import java.util.Optional.empty

class Slower : Rule() {

  override fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>> {
    return AverageDailyMotionMap.getDailySpeedOpt(planet).flatMap { dailyDeg ->
      h.getPositionOpt(planet).map<Double> { it.speedLng }.flatMap { speedLng ->
        if (speedLng < dailyDeg) {
          logger.debug("{} 每日移動速度比平均值還慢", planet)
          Optional.of<Tuple2<String, Array<Any>>>(Tuple.tuple<String, Array<Any>>("comment", arrayOf<Any>(planet)))
        }
        empty<Tuple2<String, Array<Any>>>()
      }
    }
  }

  override fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return AverageDailyMotionMap.getAvgDailySpeed(planet)?.let{ dailyDeg ->
      h.getPosition(planet)?.speedLng?.let { speedLng ->
        speedLng < dailyDeg
      }?:false
    }?.let { "comment" to arrayOf<Any>(planet) }
  }
}
